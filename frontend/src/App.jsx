
import { useEffect, useState } from 'react'
import { Routes, Route, Navigate, useLocation } from 'react-router-dom'

import Navbar from './components/Navbar'
import Home from './pages/Home'
import BrowseItems from './pages/BrowseItems'
import ItemDetails from './pages/ItemDetails'
import Login from './pages/Login'
import VerifyLogin from './pages/VerifyLogin'
import CreateItem from './pages/CreateItem'
import MyListings from './pages/MyListings'
import EditItem from './pages/EditItem'

import { getCurrentUser } from './services/authService'

import './App.css'

function App() {
  const location = useLocation()
  const [user, setUser] = useState(null)
  const [authLoading, setAuthLoading] = useState(true)

  useEffect(() => {
    async function checkAuthentication() {
      try {
        const currentUser = await getCurrentUser()
        setUser(currentUser)
      } catch (error) {
        console.error('Failed to check authentication:', error)
      } finally {
        setAuthLoading(false)
      }
    }

    checkAuthentication()
  }, [])

  return (
    <div className="app">
      <Navbar
        user={user}
        setUser={setUser}
        authLoading={authLoading}
      />

      <Routes>
        <Route path="/" element={<Home />} />
        <Route
          path="/items"
          element={
            authLoading ? (
              <main className="browse-page">
                <p>Checking authentication...</p>
              </main>
            ) : user ? (
              <BrowseItems setUser={setUser} />
            ) : (
              <Navigate
                to="/login"
                state={{ from: location.pathname + location.search }}
                replace
              />
            )
          }
        />

        <Route
          path="/items/:id"
          element={
            authLoading ? (
              <main className="browse-page">
                <p>Checking authentication...</p>
              </main>
            ) : user ? (
              <ItemDetails
                user={user}
                setUser={setUser}
              />
            ) : (
              <Navigate
                to="/login"
                state={{ from: location.pathname + location.search }}
                replace
              />
            )
          }
        />
        <Route
          path="/items/new"
          element={
            authLoading ? (
              <main className="create-item-page">
                <p>Checking authentication...</p>
              </main>
            ) : user ? (
              <CreateItem />
            ) : (
              <Navigate
                to="/login"
                state={{ from: location.pathname + location.search }}
                replace
              />
            )
          }
        />
        <Route
          path="/items/:id/edit"
          element={
            authLoading ? (
              <main className="create-item-page">
                <p>Checking authentication...</p>
              </main>
            ) : user ? (
              <EditItem setUser={setUser} />
            ) : (
              <Navigate
                to="/login"
                state={{ from: location.pathname + location.search }}
                replace
              />
            )
          }
        />
        <Route
          path="/login"
          element={
            authLoading ? (
              <main className="auth-page">
                <p>Checking authentication...</p>
              </main>
            ) : user ? (
              <Navigate to="/" replace />
            ) : (
              <Login />
            )
          }
        />
        <Route
          path="/verify-login"
          element={
            authLoading ? (
              <main className="auth-page">
                <p>Checking authentication...</p>
              </main>
            ) : user ? (
              <Navigate to="/" replace />
            ) : (
              <VerifyLogin setUser={setUser} />
            )
          }
        />
        <Route
          path="/my-listings"
          element={
            authLoading ? (
              <main className="browse-page">
                <p>Checking authentication...</p>
              </main>
            ) : user ? (
              <MyListings
                user={user}
                setUser={setUser}
              />
            ) : (
              <Navigate
                to="/login"
                state={{ from: location.pathname + location.search }}
                replace
              />
            )
          }
        />
      </Routes>
    </div>
  )
}

export default App
