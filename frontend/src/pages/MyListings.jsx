
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import ItemCard from '../components/ItemCard'
import { getMyItems, changeItemReportStatus } from '../services/itemService'

function MyListings({ setUser }) {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [updatingItemId, setUpdatingItemId] = useState(null)

    useEffect(() => {
        let active = true

        async function loadMyListings() {
            try {
                setLoading(true)
                setError('')

                const data = await getMyItems()

                if (active) {
                    setItems(data)
                }
            } catch (error) {
                if (active) {
                    if (error.message === 'SESSION_EXPIRED') {
                        setUser(null)
                    } else {
                        console.error('Failed to load listings:', error)
                        setError('Unable to load your listings.')
                    }
                }
            } finally {
                if (active) {
                    setLoading(false)
                }
            }
        }

        loadMyListings()

        return () => {
            active = false
        }
    }, [setUser])

    async function handleCloseListing(id) {
        if (updatingItemId !== null) {
            return
        }

        const confirmed = window.confirm(
            'Are you sure you want to close this listing? ' +
            'This action cannot be undone.'
        )

        if (!confirmed) {
            return
        }

        setUpdatingItemId(id)
        setActionError('')

        try {
            const updatedItem = await changeItemReportStatus(
                id,
                'CLOSED'
            )

            setItems((previousItems) =>
                previousItems.map((item) =>
                    item.id === id ? updatedItem : item
                )
            )
        } catch (error) {
            console.error('Failed to close listing:', error)

            if (error.message === 'SESSION_EXPIRED') {
                setUser(null)
            } else if (error.message === 'FORBIDDEN') {
                setActionError(
                    'You do not have permission to close this listing.'
                )
            } else if (error.message === 'CONFLICT') {
                setActionError(
                    'This listing cannot be closed. It may have an active claim or may no longer be open.'
                )
            } else if (error.message === 'Item not found') {
                setActionError('This listing no longer exists.')
            } else {
                setActionError(
                    'Failed to close listing. Please try again.'
                )
            }
        } finally {
            setUpdatingItemId(null)
        }
    }

    return (
        <main className="browse-page">
            <div className="browse-container">
                <h1>My Listings</h1>

                <p>
                    Manage the lost and found items you have reported.
                </p>

                <Link to="/items/new" className="report-item-button">
                    + Report an Item
                </Link>

                {actionError && (
                    <p role="alert" className="listing-action-error">
                        {actionError}
                    </p>
                )}

                {loading ? (
                    <div className="no-items">
                        <p>Loading your listings...</p>
                    </div>
                ) : error ? (
                    <div className="no-items">
                        <h2>Something went wrong</h2>
                        <p>{error}</p>
                    </div>
                ) : items.length > 0 ? (
                    <div className="items-grid">
                        {items.map((item) => (
                            <ItemCard
                                key={item.id}
                                item={item}
                                showActions={true}
                                onClose={handleCloseListing}
                                isUpdating={updatingItemId === item.id}
                                actionsDisabled={updatingItemId !== null}
                            />
                        ))}
                    </div>
                ) : (
                    <div className="no-items">
                        <h2>No listings yet</h2>
                        <p>
                            You haven't reported any lost or found items yet.
                        </p>
                    </div>
                )}
            </div>
        </main>
    )
}

export default MyListings
