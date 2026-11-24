
import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import ItemCard from '../components/ItemCard'
import { getAllItems } from '../services/itemService'

function BrowseItems({ setUser }) {
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedType = searchParams.get('type')

  // Items loaded from the backend
  const [items, setItems] = useState([])

  // Search text
  const [searchTerm, setSearchTerm] = useState('')

  // Loading and error states
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  // Load items when the page opens
  useEffect(() => {
    let cancelled = false

    async function loadItems() {
      try {
        const data = await getAllItems()

        if (!cancelled) {
          setItems(data)
        }
      } catch (error) {
        if (!cancelled) {
          if (error.message === 'SESSION_EXPIRED') {
            setUser(null)
          } else {
            console.error('Failed to load items:', error)
            setError('Failed to load items. Please try again.')
          }
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    loadItems()

    return () => {
      cancelled = true
    }
  }, [setUser])

  // Filter items by type and search text
  const filteredItems = items.filter((item) => {
    const matchesType =
      !selectedType ||
      item.itemReportType === selectedType

    const search = searchTerm.toLowerCase().trim()

    const matchesSearch =
      (item.itemName ?? '').toLowerCase().includes(search) ||
      (item.itemDetails ?? '').toLowerCase().includes(search) ||
      (item.location ?? '').toLowerCase().includes(search) ||
      (item.category ?? '').toLowerCase().includes(search) ||
      (item.itemReportType ?? '').toLowerCase().includes(search) ||
      (item.brand ?? '').toLowerCase().includes(search) ||
      (item.colour ?? '').toLowerCase().includes(search)

    return matchesType && matchesSearch
  })

  // Update the URL filter
  function handleFilterChange(type) {
    if (type === 'ALL') {
      setSearchParams({})
    } else {
      setSearchParams({ type })
    }
  }

  return (
    <main className="browse-page">
      <div className="browse-container">


        <div className="browse-header">
          <div>
            <h1>Browse Items</h1>
            <p>
              Discover lost and found items from your university.
            </p>
          </div>

          <Link to="/items/new" className="report-item-button">
            + Report an Item
          </Link>
        </div>


        {/* Filter buttons */}
        <div className="filter-buttons">
          <button
            type="button"
            className={!selectedType ? 'active' : ''}
            onClick={() => handleFilterChange('ALL')}
          >
            All Items
          </button>

          <button
            type="button"
            className={selectedType === 'LOST' ? 'active' : ''}
            onClick={() => handleFilterChange('LOST')}
          >
            Lost Items
          </button>

          <button
            type="button"
            className={selectedType === 'FOUND' ? 'active' : ''}
            onClick={() => handleFilterChange('FOUND')}
          >
            Found Items
          </button>
        </div>

        {/* Search bar */}
        <div className="search-container">
          <input
            type="text"
            placeholder="Search by item name, description or location..."
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            className="search-input"
            aria-label="Search items"
          />
        </div>

        {/* Loading, error or item cards */}
        {loading ? (
          <div className="no-items">
            <p>Loading items...</p>
          </div>
        ) : error ? (
          <div className="no-items">
            <h2>Something went wrong</h2>
            <p>{error}</p>
          </div>
        ) : filteredItems.length > 0 ? (
          <div className="items-grid">
            {filteredItems.map((item) => (
              <ItemCard
                key={item.id}
                item={item}
              />
            ))}
          </div>
        ) : (
          <div className="no-items">
            <h2>No items found</h2>
            <p>
              Try a different search term or change your filters.
            </p>
          </div>
        )}

      </div>
    </main>
  )
}

export default BrowseItems
