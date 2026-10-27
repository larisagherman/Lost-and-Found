
import { useParams } from 'react-router-dom'

function ItemDetails() {
  const { id } = useParams()

  return (
    <main>
      <h1>Item Details</h1>
      <p>Item ID: {id}</p>
    </main>
  )
}

export default ItemDetails
