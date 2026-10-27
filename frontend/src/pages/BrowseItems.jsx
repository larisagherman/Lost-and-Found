
import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import ItemCard from '../components/ItemCard'

// Temporary data used until backend API integration
const testItems = [
  {
    id: 1,
    itemName: 'Silver Keys',
    itemDetails: 'Three keys on a red fabric keyring.',
    category: 'PERSONAL belongings',
    location: 'UPT Faculty of Automation and Computers',
    itemReportType: 'FOUND',
    incidentAt: '2026-10-16T08:45:00'
  },
  {
    id: 2,
    itemName: 'Black Headphones',
    itemDetails: 'Black wireless headphones lost near the library.',
    category: 'Electronics',
    location: 'University Library',
    itemReportType: 'LOST',
    incidentAt: '2026-10-20T12:30:00'
  }
]

function BrowseItems() {
  // Read and update URL query parameters
  const [searchParams, setSearchParams] = useSearchParams()
  const selectedType = searchParams.get('type')

  // Store the text entered in the search bar
  const [searchTerm, setSearchTerm] = useState('')

  // Filter items by report type and search text
  const filteredItems = testItems.filter((item) => {
    const matchesType =
      !selectedType ||
      item.itemReportType === selectedType

    const search = searchTerm.toLowerCase().trim()

    const matchesSearch =
      (item.itemName ?? '').toLowerCase().includes(search) ||
      (item.itemDetails ?? '').toLowerCase().includes(search) ||
      (item.location ?? '').toLowerCase().includes(search) ||
      (item.category ?? '').toLowerCase().includes(search) ||
      (item.itemReportType ?? '').toLowerCase().includes(search)

    return matchesType && matchesSearch
  })

  // Update the selected filter in the URL
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

        <h1>Browse Items</h1>

        <p>
          Discover lost and found items from your university.
        </p>

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

        {/* Item cards or empty state */}
        {filteredItems.length > 0 ? (
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
