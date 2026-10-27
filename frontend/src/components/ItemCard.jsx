
import { Link } from 'react-router-dom'
import './ItemCard.css'

function ItemCard({ item }) {
  const isLost = item.itemReportType === 'LOST'

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
          className={`item-badge ${
            isLost ? 'badge-lost' : 'badge-found'
          }`}
        >
          {isLost ? 'LOST' : 'FOUND'}
        </span>
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

      </div>
    </article>
  )
}

export default ItemCard
