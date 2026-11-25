
import { useEffect, useState, useRef } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getItemById } from '../services/itemService'
import { submitClaim, getMyClaims } from '../services/claimService'
import './ItemDetails.css'


function ItemDetails({ user, setUser }) {
  const { id } = useParams()

  return (
    <ItemDetailsContent
      key={`${user?.id}-${id}`}
      id={id}
      user={user}
      setUser={setUser}
    />
  )
}


function ItemDetailsContent({ id, user, setUser }) {
  const [item, setItem] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showClaimForm, setShowClaimForm] = useState(false)
  const [proofDetails, setProofDetails] = useState('')
  const [claimSubmitting, setClaimSubmitting] = useState(false)
  const [claimError, setClaimError] = useState('')
  const [claimSuccess, setClaimSuccess] = useState('')
  const [existingClaim, setExistingClaim] = useState(null)
  const [claimsLoading, setClaimsLoading] = useState(true)
  const [claimsLoadError, setClaimsLoadError] = useState('')

  const isMounted = useRef(false)

  useEffect(() => {
    isMounted.current = true

    return () => {
      isMounted.current = false
    }
  }, [])

  const isOwner =
    user != null &&
    item != null &&
    String(item.reporterId) === String(user.id)

  const canClaimItem =
    user?.role === 'STUDENT' &&
    item != null &&
    String(item.id) === String(id) &&
    item.itemReportType === 'FOUND' &&
    item.itemReportStatus === 'OPEN' &&
    !isOwner

  useEffect(() => {
    let active = true

    async function loadItem() {
      try {
        setLoading(true)
        setError('')
        setItem(null)
        setShowClaimForm(false)
        setProofDetails('')
        setClaimError('')
        setClaimSuccess('')
        setExistingClaim(null)
        setClaimsLoading(true)
        setClaimsLoadError('')

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


  useEffect(() => {
    let active = true

    async function loadExistingClaim() {
      setExistingClaim(null)
      setClaimsLoadError('')

      if (!canClaimItem) {
        setClaimsLoading(false)
        return
      }

      try {
        setClaimsLoading(true)

        const claims = await getMyClaims()

        const matchingClaim = claims.find(
          (claim) =>
            String(claim.foundItemReportId) === String(id)
        )

        if (active) {
          setExistingClaim(matchingClaim || null)
        }

      } catch (error) {
        if (active) {
          if (error.message === 'SESSION_EXPIRED') {
            setUser(null)
          } else {
            console.error('Failed to check existing claims:', error)

            setClaimsLoadError(
              'Unable to check your existing claims. Please refresh the page.'
            )
          }
        }

      } finally {
        if (active) {
          setClaimsLoading(false)
        }
      }
    }

    loadExistingClaim()

    return () => {
      active = false
    }
  }, [id, user?.id, canClaimItem, setUser])


  async function handleSubmitClaim(event) {
    event.preventDefault()

    if (
      !canClaimItem ||
      claimsLoading ||
      claimsLoadError ||
      existingClaim ||
      claimSubmitting
    ) {
      return
    }

    setClaimError('')
    setClaimSuccess('')

    const trimmedProof = proofDetails.trim()

    if (!trimmedProof) {
      setClaimError('Please provide proof of ownership.')
      return
    }

    if (trimmedProof.length > 2000) {
      setClaimError(
        'Proof of ownership cannot exceed 2000 characters.'
      )
      return
    }

    try {
      setClaimSubmitting(true)

      const claim = await submitClaim(id, trimmedProof)

      if (!isMounted.current) {
        return
      }

      setExistingClaim(claim)
      setClaimSuccess('Your claim has been submitted successfully.')
      setProofDetails('')
      setShowClaimForm(false)

    } catch (error) {
      if (!isMounted.current) {
        return
      }

      if (error.message === 'SESSION_EXPIRED') {
        setUser(null)

      } else if (error.message === 'BAD_REQUEST') {
        setClaimError('Please check your proof of ownership.')

      } else if (error.message === 'FORBIDDEN') {
        setClaimError('You are not allowed to claim this item.')

      } else if (error.message === 'Item not found') {
        setClaimError('This item is no longer available.')

      } else if (error.message === 'CONFLICT') {
        setClaimError(
          'This claim cannot be submitted. Please refresh the page or contact an institution administrator.'
        )

      } else {
        console.error('Failed to submit claim:', error)

        setClaimError(
          'Unable to submit your claim. Please try again later.'
        )
      }

    } finally {
      if (isMounted.current) {
        setClaimSubmitting(false)
      }
    }
  }

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

          {canClaimItem && (
            <section className="item-details-section claim-section">
              {claimSuccess && (
                <p className="claim-success" role="status">
                  {claimSuccess}
                </p>
              )}
              {claimsLoading ? (
                <p>Checking your claims...</p>

              ) : claimsLoadError ? (
                <p className="claim-error" role="alert">
                  {claimsLoadError}
                </p>

              ) : existingClaim ? (
                <div className="existing-claim">
                  <h2>Claim already submitted</h2>

                  <p>
                    You have already submitted a claim for this item.
                  </p>

                  <p>
                    <strong>Claim Status:</strong> {existingClaim.status}
                  </p>
                </div>

              ) : (
                <>
                  <h2>Is this your item?</h2>

                  <p>
                    Submit a claim with identifying details to help
                    verify your ownership.
                  </p>

                  {!showClaimForm && (
                    <button
                      type="button"
                      className="claim-button"
                      onClick={() => {
                        setShowClaimForm(true)
                        setClaimError('')
                        setClaimSuccess('')
                      }}
                    >
                      Claim Item
                    </button>
                  )}

                  {showClaimForm && (
                    <form
                      className="claim-form"
                      onSubmit={handleSubmitClaim}
                    >
                      <label htmlFor="proofDetails">
                        Proof of ownership *
                      </label>

                      <textarea
                        id="proofDetails"
                        name="proofDetails"
                        value={proofDetails}
                        onChange={(event) => {
                          setProofDetails(event.target.value)
                          setClaimError('')
                        }}
                        placeholder="Describe unique identifying details about your item..."
                        rows={5}
                        maxLength={2000}
                        required
                        disabled={claimSubmitting}
                      />

                      <span className="claim-character-count">
                        {proofDetails.length}/2000
                      </span>

                      <p className="claim-privacy-note">
                        Your verification details will not be displayed publicly.
                      </p>

                      {claimError && (
                        <p className="claim-error" role="alert">
                          {claimError}
                        </p>
                      )}

                      <div className="claim-form-actions">
                        <button
                          type="submit"
                          className="claim-button"
                          disabled={claimSubmitting}
                        >
                          {claimSubmitting ? 'Submitting...' : 'Submit Claim'}
                        </button>

                        <button
                          type="button"
                          className="claim-cancel-button"
                          disabled={claimSubmitting}
                          onClick={() => {
                            setShowClaimForm(false)
                            setProofDetails('')
                            setClaimError('')
                          }}
                        >
                          Cancel
                        </button>
                      </div>
                    </form>
                  )}

                </>
              )}

            </section>
          )}

        </article>
      </div>
    </main>
  )

}

export default ItemDetails
