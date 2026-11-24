
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import ItemCard from '../components/ItemCard'
import { getAllItems } from '../services/itemService'

function MyListings({ user, setUser }) {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    async function loadMyListings() {
      try {
        setLoading(true)
        setError('')

        const data = await getAllItems()

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

  const myItems = items.filter(
    (item) =>
      user != null &&
      item.reporterId != null &&
      String(item.reporterId) === String(user.id)
  )

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

        {loading ? (
          <div className="no-items">
            <p>Loading your listings...</p>
          </div>
        ) : error ? (
          <div className="no-items">
            <h2>Something went wrong</h2>
            <p>{error}</p>
          </div>
        ) : myItems.length > 0 ? (
          <div className="items-grid">
            {myItems.map((item) => (
              <ItemCard key={item.id} item={item} />
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
