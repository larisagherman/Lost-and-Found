
import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { requestLoginCode } from '../services/authService'
import './Auth.css'

function Login() {
    const navigate = useNavigate()
    const location = useLocation()
    const returnTo = location.state?.from || '/'

    const [email, setEmail] = useState('')
    const [message, setMessage] = useState('')
    const [loading, setLoading] = useState(false)

    async function handleSubmit(event) {
        event.preventDefault()

        setLoading(true)
        setMessage('')

        try {
            await requestLoginCode(email)
            navigate('/verify-login', {
                state: { from: returnTo }
            })
        } catch (error) {
            setMessage('Something went wrong. Please try again.')
            console.error(error)
        } finally {
            setLoading(false)
        }
    }

    return (
        <main className="auth-page">
            <div className="auth-card">

                <div className="auth-header">
                    <div className="auth-icon">
                        ✦
                    </div>

                    <h1>Student Login</h1>

                    <p>
                        Enter your university email to receive
                        a sign-in code.
                    </p>
                </div>

                <form
                    className="auth-form"
                    onSubmit={handleSubmit}
                >
                    <label htmlFor="email">
                        University Email
                    </label>

                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        placeholder="name@student.upt.ro"
                        autoComplete="email"
                        required
                    />

                    <button
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Sending...' : 'Send Login Code'}
                    </button>
                </form>

                {message && (
                    <p className="auth-message" role="alert">
                        {message}
                    </p>
                )}

                <div className="auth-footer">
                    For UPT and UVT students
                </div>

            </div>
        </main>
    )
}

export default Login
