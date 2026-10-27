
import { Routes, Route } from 'react-router-dom'

import Navbar from './components/Navbar'
import Home from './pages/Home'
import BrowseItems from './pages/BrowseItems'
import ItemDetails from './pages/ItemDetails'

import './App.css'

function App() {
  return (
    <div className="app">
      <Navbar />

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/items" element={<BrowseItems />} />
        <Route path="/items/:id" element={<ItemDetails />} />
      </Routes>
    </div>
  )
}

export default App
