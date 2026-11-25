
import { API_BASE_URL, getCsrfHeaders } from './itemService'

export async function submitClaim(reportId, proofDetails) {
    const headers = await getCsrfHeaders()

    const response = await fetch(
        `${API_BASE_URL}/item-reports/${reportId}/claims`,
        {
            method: 'POST',
            credentials: 'include',
            headers,
            body: JSON.stringify({
                proofDetails: proofDetails.trim()
            })
        }
    )

    if (response.status === 400) {
        throw new Error('BAD_REQUEST')
    }

    if (response.status === 401) {
        throw new Error('SESSION_EXPIRED')
    }

    if (response.status === 403) {
        throw new Error('FORBIDDEN')
    }

    if (response.status === 404) {
        throw new Error('Item not found')
    }

    if (response.status === 409) {
        throw new Error('CONFLICT')
    }

    if (!response.ok) {
        throw new Error('Failed to submit claim')
    }

    return await response.json()
}

export async function getMyClaims() {
    const response = await fetch(
        `${API_BASE_URL}/claims/mine`,
        {
            method: 'GET',
            credentials: 'include'
        }
    )

    if (response.status === 401) {
        throw new Error('SESSION_EXPIRED')
    }

    if (response.status === 403) {
        throw new Error('FORBIDDEN')
    }

    if (!response.ok) {
        throw new Error('Failed to fetch claims')
    }

    return await response.json()
}
