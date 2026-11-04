
import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getItemById } from '../services/itemService'
import './ItemDetails.css'

function ItemDetails({ user, setUser }) {
  const { id } = useParams()

  const [item, setItem] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    async function loadItem() {
      try {
        setLoading(true)
        setError('')
        setItem(null)

        const data = await getItemById(id)

        if (active) {
          setItem(data)
        }

      } catch (error) {
        if (active) {
          if (error.message === 'SESSION_EXPIRED') {
            setUser(null)
          } else if (error.message === 'Item not found') {
            setError('This item does not exist.')
          } else {
            console.error('Failed to load item:', error)
            setError('Unable to load item details. Please try again later.')
          }
        }
      }
      finally {
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

  if (loading) {
    return (
      <main className="browse-page">
        <p>Loading item details...</p>
      </main>
    )
  }

  if (error) {
    const itemNotFound = error === 'This item does not exist.'

    return (
      <main className="browse-page">
        <h1>
          {itemNotFound ? 'Item Not Found' : 'Something went wrong'}
        </h1>

        <p role="alert">{error}</p>

        <Link to="/items">
          ← Back to Browse Items
        </Link>
      </main>
    )
  }

  if (!item) {
    return null
  }
  const isOwner =
    user != null &&
    String(item.reporterId) === String(user.id)

  return (
    <main className="item-details-page">
      <div className="item-details-container">

        <Link to="/items" className="item-details-back">
          ← Back to Browse Items
        </Link>

        <article className="item-details-card">

          <span
            className={`item-details-badge ${item.itemReportType === 'FOUND' ? 'found' : 'lost'
              }`}
          >
            {item.itemReportType}
          </span>

          <h1>{item.itemName}</h1>
          {isOwner && (
            <p className="item-owner-label">
              Your listing
            </p>
          )}

          <p className="item-details-category">
            {item.category}
          </p>

          <section className="item-details-section">
            <h2>Description</h2>
            <p>{item.itemDetails || 'No description available.'}</p>
          </section>


          {(item.brand || item.colour || item.itemReportStatus) && (
            <section className="item-details-section">
              <h2>Additional Information</h2>

              {item.brand && (
                <p>
                  <strong>Brand:</strong> {item.brand}
                </p>
              )}

              {item.colour && (
                <p>
                  <strong>Colour:</strong> {item.colour}
                </p>
              )}

              {item.itemReportStatus && (
                <p>
                  <strong>Report Status:</strong> {item.itemReportStatus}
                </p>
              )}
            </section>
          )}


          <section className="item-details-section item-details-info">
            <div>
              <h2>Location</h2>
              <p>📍 {item.location || 'Not specified'}</p>
            </div>

            <div>
              <h2>Incident Date</h2>
              <p>
                📅{' '}
                {item.incidentAt
                  ? new Date(item.incidentAt).toLocaleDateString('en-GB', {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric'
                  })
                  : 'Not specified'}
              </p>
            </div>

            <div>
              <h2>Published On</h2>
              <p>
                📅{' '}
                {item.createdAt
                  ? new Date(item.createdAt).toLocaleDateString('en-GB', {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric'
                  })
                  : 'Not specified'}
              </p>
            </div>
          </section>

        </article>
      </div>
    </main>
  )

}

export default ItemDetails
