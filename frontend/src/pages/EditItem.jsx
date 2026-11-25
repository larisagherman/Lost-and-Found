
import { useEffect, useState } from 'react'
import { useNavigate, useParams, Link } from 'react-router-dom'
import { getMyItems, updateItemReport } from '../services/itemService'
import './CreateItem.css'

function EditItem({ setUser }) {
    const { id } = useParams()
    const navigate = useNavigate()

    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState('')
    const [formData, setFormData] = useState(null)

    function getCurrentLocalDateTime() {
        const now = new Date()

        const year = now.getFullYear()
        const month = String(now.getMonth() + 1).padStart(2, '0')
        const day = String(now.getDate()).padStart(2, '0')
        const hours = String(now.getHours()).padStart(2, '0')
        const minutes = String(now.getMinutes()).padStart(2, '0')

        return `${year}-${month}-${day}T${hours}:${minutes}`
    }

    useEffect(() => {
        let active = true

        async function loadItem() {
            setLoading(true)
            setError('')
            setFormData(null)

            try {
                const items = await getMyItems()

                if (!active) return

                const item = items.find(
                    (item) => String(item.id) === String(id)
                )

                if (!item) {
                    setError('Item not found or you do not have permission to edit it.')
                    return
                }

                if (item.itemReportStatus !== 'OPEN') {
                    setError('Only open reports can be edited.')
                    return
                }

                setFormData({
                    itemName: item.itemName ?? '',
                    itemDetails: item.itemDetails ?? '',
                    privateVerificationDetails: '',
                    category: item.category ?? '',
                    colour: item.colour ?? '',
                    brand: item.brand ?? '',
                    incidentAt: item.incidentAt
                        ? item.incidentAt.slice(0, 16)
                        : '',
                    itemReportType: item.itemReportType,
                    location: item.location ?? ''
                })

            } catch (error) {
                if (!active) return

                if (error.message === 'SESSION_EXPIRED') {
                    setUser(null)
                } else {
                    setError('Failed to load item report.')
                }
            } finally {
                if (active) {
                    setLoading(false)
                }
            }
        }

        loadItem()

        return () => {
            active = false
        }
    }, [id, setUser])

    function handleChange(event) {
        const { name, value } = event.target

        setFormData((previousData) => ({
            ...previousData,
            [name]: value
        }))
    }

    async function handleSubmit(event) {
        event.preventDefault()
        setError('')

        if (
            formData.itemReportType === 'FOUND' &&
            !formData.privateVerificationDetails.trim()
        ) {
            setError('Please provide private verification details.')
            return
        }

        setSaving(true)

        try {
            const itemData = {
                ...formData,
                privateVerificationDetails:
                    formData.itemReportType === 'FOUND'
                        ? formData.privateVerificationDetails.trim()
                        : null,
                incidentAt: formData.incidentAt
                    ? `${formData.incidentAt}:00`
                    : null
            }

            await updateItemReport(id, itemData)

            navigate('/my-listings')

        } catch (error) {
            console.error('Failed to update item:', error)

            if (error.message === 'SESSION_EXPIRED') {
                setUser(null)
            } else if (error.message === 'FORBIDDEN') {
                setError('You do not have permission to edit this report.')
            } else if (error.message === 'CONFLICT') {
                setError(
                    'This report cannot be updated because of its current status or existing claims.'
                )
            } else if (error.message === 'Item not found') {
                setError('This report no longer exists.')
            } else {
                setError('Failed to update item report. Please try again.')
            }
        } finally {
            setSaving(false)
        }
    }

    if (loading) {
        return (
            <main className="create-item-page">
                <p>Loading item report...</p>
            </main>
        )
    }

    if (!formData) {
        return (
            <main className="create-item-page">
                <p role="alert" className="create-item-error">
                    {error}
                </p>

                <Link to="/my-listings">
                    Back to My Listings
                </Link>
            </main>
        )
    }

    return (
        <main className="create-item-page">
            <h1>Edit Item Report</h1>

            <p>
                Update the information about your reported item.
            </p>

            <form onSubmit={handleSubmit}>

                <label htmlFor="itemReportType">
                    Report Type
                </label>

                <select
                    id="itemReportType"
                    name="itemReportType"
                    value={formData.itemReportType}
                    onChange={handleChange}
                >
                    <option value="LOST">Lost Item</option>
                    <option value="FOUND">Found Item</option>
                </select>

                <label htmlFor="itemName">Item Name</label>

                <input
                    id="itemName"
                    name="itemName"
                    value={formData.itemName}
                    onChange={handleChange}
                    required
                />

                <label htmlFor="itemDetails">Description</label>

                <textarea
                    id="itemDetails"
                    name="itemDetails"
                    value={formData.itemDetails}
                    onChange={handleChange}
                    required
                />

                {formData.itemReportType === 'FOUND' && (
                    <>
                        <label htmlFor="privateVerificationDetails">
                            Private Verification Details
                        </label>

                        <textarea
                            id="privateVerificationDetails"
                            name="privateVerificationDetails"
                            placeholder="Re-enter the private verification details..."
                            value={formData.privateVerificationDetails}
                            onChange={handleChange}
                            required
                        />

                        <small className="private-details-hint">
                            For privacy reasons, existing verification
                            details are not displayed. Re-enter the
                            details before saving. The new value will
                            replace the previous one.
                        </small>
                    </>
                )}

                <label htmlFor="category">Category</label>

                <select
                    id="category"
                    name="category"
                    value={formData.category}
                    onChange={handleChange}
                    required
                >
                    <option value="">Select a category</option>
                    <option value="ELECTRONICS">Electronics</option>
                    <option value="DOCUMENTS">Documents</option>
                    <option value="PERSONAL BELONGINGS">Personal Belongings</option>
                    <option value="STUDY MATERIALS">Study Materials</option>
                    <option value="CLOTHING">Clothing & Accessories</option>
                    <option value="OTHER">Other</option>
                </select>

                <label htmlFor="colour">Colour</label>

                <input
                    id="colour"
                    name="colour"
                    value={formData.colour}
                    onChange={handleChange}
                />

                <label htmlFor="brand">Brand</label>

                <input
                    id="brand"
                    name="brand"
                    value={formData.brand}
                    onChange={handleChange}
                />

                <label htmlFor="incidentAt">Incident Date</label>

                <input
                    id="incidentAt"
                    name="incidentAt"
                    type="datetime-local"
                    max={getCurrentLocalDateTime()}
                    value={formData.incidentAt}
                    onChange={handleChange}
                    required
                />

                <label htmlFor="location">Location</label>

                <input
                    id="location"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                    required
                />

                {error && (
                    <p role="alert" className="create-item-error">
                        {error}
                    </p>
                )}

                <button type="submit" disabled={saving}>
                    {saving ? 'Saving Changes...' : 'Save Changes'}
                </button>

                <button
                    type="button"
                    className="edit-item-cancel"
                    disabled={saving}
                    onClick={() => navigate('/my-listings')}
                >
                    Cancel
                </button>

            </form>
        </main>
    )
}

export default EditItem
