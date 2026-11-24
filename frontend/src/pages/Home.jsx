
import { Link } from 'react-router-dom'

function Home() {
  return (
    <main className="home">
      <section className="hero">
        <h1>Find what matters.</h1>

        <p>
          A platform for UPT and UVT students to report
          lost items and help others find their belongings.
        </p>

        <div className="hero-buttons">
          <Link to="/items?type=LOST">Lost Items</Link>
          <Link to="/items?type=FOUND">Found Items</Link>
        </div>
      </section>
    </main>
  )
}

export default Home
