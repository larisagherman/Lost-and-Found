
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createItemReport } from '../services/itemService'

function CreateItem() {
    const navigate = useNavigate()

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [formData, setFormData] = useState({
        itemName: '',
        itemDetails: '',
        category: '',
        colour: '',
        brand: '',
        incidentAt: '',
        itemReportType: 'LOST',
        location: ''
    })

    function handleChange(event) {
        const { name, value } = event.target

        setFormData((previousData) => ({
            ...previousData,
            [name]: value
        }))
    }
    async function handleSubmit(event) {
        event.preventDefault()

        setLoading(true)
        setError('')

        try {
            const itemData = {
                ...formData,
                incidentAt: formData.incidentAt
                    ? `${formData.incidentAt}:00`
                    : null
            }

            const createdItem = await createItemReport(itemData)

            console.log('Created item:', createdItem)

            navigate('/items')
        } catch (error) {
            console.error('Failed to create item:', error)

            if (error.message === 'SESSION_EXPIRED') {
                setError('Your session has expired. Please log in again.')
            } else if (error.message === 'FORBIDDEN') {
                setError('You do not have permission to create this report.')
            } else {
                setError('Failed to create item report. Please try again.')
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <main className="create-item-page">
            <h1>Create Item Report</h1>

            <p>
                Report a lost or found item to help students
                recover their belongings.
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
                />

                <label htmlFor="category">Category</label>
                <input
                    id="category"
                    name="category"
                    value={formData.category}
                    onChange={handleChange}
                />

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

                <label htmlFor="incidentAt">
                    Incident Date
                </label>
                <input
                    id="incidentAt"
                    name="incidentAt"
                    type="datetime-local"
                    value={formData.incidentAt}
                    onChange={handleChange}
                />

                <label htmlFor="location">Location</label>
                <input
                    id="location"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                />

                <button type="submit" disabled={loading}>
                    {loading ? 'Creating Report...' : 'Create Report'}
                </button>
            </form>
            {error && (
                <p role="alert" className="create-item-error">
                    {error}
                </p>
            )}
        </main>
    )
}

export default CreateItem
