
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createItemReport } from '../services/itemService'
import './CreateItem.css'

function CreateItem() {
    const navigate = useNavigate()

    function getCurrentLocalDateTime() {
        const now = new Date()

        const year = now.getFullYear()
        const month = String(now.getMonth() + 1).padStart(2, '0')
        const day = String(now.getDate()).padStart(2, '0')
        const hours = String(now.getHours()).padStart(2, '0')
        const minutes = String(now.getMinutes()).padStart(2, '0')

        return `${year}-${month}-${day}T${hours}:${minutes}`
    }

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [formData, setFormData] = useState({
        itemName: '',
        itemDetails: '',
        privateVerificationDetails: '',
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

        if (
            formData.itemReportType === 'FOUND' &&
            !formData.privateVerificationDetails.trim()
        ) {
            setError('Please provide private verification details.')
            return
        }

        setLoading(true)
        setError('')

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

            await createItemReport(itemData)

            navigate('/my-listings')
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
                    placeholder="e.g. Blue Backpack"
                    value={formData.itemName}
                    onChange={handleChange}
                    required
                />

                <label htmlFor="itemDetails">Description</label>
                <textarea
                    id="itemDetails"
                    name="itemDetails"
                    placeholder="Describe the item, including any identifying details..."
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
                            placeholder="Describe details that only the rightful owner would know..."
                            value={formData.privateVerificationDetails}
                            onChange={handleChange}
                            required
                        />

                        <small className="private-details-hint">
                            Provide details that can help verify the rightful
                            owner. Do not include these details in the
                            public description.
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
                    placeholder="e.g. Blue"
                    value={formData.colour}
                    onChange={handleChange}
                />

                <label htmlFor="brand">Brand</label>
                <input
                    id="brand"
                    name="brand"
                    placeholder="e.g. Nike, Apple, Samsung"
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
                    max={getCurrentLocalDateTime()}
                    value={formData.incidentAt}
                    onChange={handleChange}
                    required
                />

                <label htmlFor="location">Location</label>
                <input
                    id="location"
                    name="location"
                    placeholder="e.g. UPT Library, first floor"
                    value={formData.location}
                    onChange={handleChange}
                    required
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
