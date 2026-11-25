
import { Link } from 'react-router-dom'
import './ItemCard.css'

function ItemCard({
  item,
  showActions = false,
  onClose,
  isUpdating = false,
  actionsDisabled = false
}) {
  const isLost = item.itemReportType === 'LOST'
  const isOpen = item.itemReportStatus === 'OPEN'

  const formattedDate = new Date(
    item.incidentAt
  ).toLocaleDateString('en-GB', {
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  })

  return (
    <article className="item-card">

      <div className="item-card-image">
        <div className="item-image-placeholder">
          <span>📦</span>
        </div>

        <span
          className={`item-badge ${isLost ? 'badge-lost' : 'badge-found'
            }`}
        >
          {isLost ? 'LOST' : 'FOUND'}
        </span>
        {showActions && (
          <span
            className={`item-status-badge status-${item.itemReportStatus.toLowerCase()}`}
          >
            {item.itemReportStatus}
          </span>
        )}
      </div>

      <div className="item-card-content">

        <span className="item-category">
          {item.category}
        </span>

        <h3 className="item-title">
          {item.itemName}
        </h3>

        <p className="item-description">
          {item.itemDetails}
        </p>

        <div className="item-meta">
          <p>📍 {item.location}</p>
          <p>📅 {formattedDate}</p>
        </div>

        <Link
          to={`/items/${item.id}`}
          className="item-details-link"
        >
          View details
          <span aria-hidden="true">→</span>
        </Link>
        {showActions && isOpen && (
          <div className="item-card-actions">
            <Link
              to={`/items/${item.id}/edit`}
              className="item-edit-button"
            >
              Edit Listing
            </Link>

            <button
              type="button"
              className="item-close-button"
              onClick={() => onClose(item.id)}
              disabled={actionsDisabled}
            >
              {isUpdating ? 'Closing Listing...' : 'Close Listing'}
            </button>
          </div>
        )}

      </div>
    </article>
  )
}

export default ItemCard
