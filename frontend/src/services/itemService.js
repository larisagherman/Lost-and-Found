
const API_BASE_URL = 'http://localhost:8081'

async function getCsrfHeaders() {
    const response = await fetch(`${API_BASE_URL}/auth/csrf`, {
        method: 'GET',
        credentials: 'include'
    })

    if (response.status === 401) {
        throw new Error('SESSION_EXPIRED')
    }

    if (!response.ok) {
        throw new Error('Failed to fetch CSRF token')
    }

    const { token, headerName } = await response.json()

    return {
        'Content-Type': 'application/json',
        [headerName]: token
    }
}

export async function getAllItems() {
    const response = await fetch(`${API_BASE_URL}/item-reports`, {
        method: 'GET',
        credentials: 'include'
    })

    if (response.status === 401) {
        throw new Error('SESSION_EXPIRED')
    }

    if (!response.ok) {
        throw new Error('Failed to fetch items')
    }

    return await response.json()
}

export async function getMyItems() {
    const response = await fetch(`${API_BASE_URL}/item-reports/mine`, {
        method: 'GET',
        credentials: 'include'
    })

    if (response.status === 401) {
        throw new Error('SESSION_EXPIRED')
    }

    if (!response.ok) {
        throw new Error('Failed to fetch my items')
    }

    return await response.json()
}

export async function getItemById(id) {
    const items = await getAllItems()

    const item = items.find(
        (item) => String(item.id) === String(id)
    )

    if (item) {
        return item
    }

    const myItems = await getMyItems()

    const myItem = myItems.find(
        (item) => String(item.id) === String(id)
    )

    if (!myItem) {
        throw new Error('Item not found')
    }

    return myItem
}

export async function createItemReport(itemData) {
    const headers = await getCsrfHeaders()

    const response = await fetch(`${API_BASE_URL}/item-reports`, {
        method: 'POST',
        credentials: 'include',
        headers,
        body: JSON.stringify(itemData)
    })

    if (response.status === 401) {
        throw new Error('SESSION_EXPIRED')
    }

    if (response.status === 403) {
        throw new Error('FORBIDDEN')
    }

    if (!response.ok) {
        throw new Error('Failed to create item report')
    }

    return await response.text()
}

export async function updateItemReport(id, itemData) {
    const headers = await getCsrfHeaders()

    const response = await fetch(
        `${API_BASE_URL}/item-reports/${id}`,
        {
            method: 'PUT',
            credentials: 'include',
            headers,
            body: JSON.stringify(itemData)
        }
    )

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
        throw new Error('Failed to update item report')
    }

    return await response.json()
}

export async function changeItemReportStatus(id, status) {
    const headers = await getCsrfHeaders()

    const response = await fetch(
        `${API_BASE_URL}/item-reports/${id}/status`,
        {
            method: 'PATCH',
            credentials: 'include',
            headers,
            body: JSON.stringify({ status })
        }
    )

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
        throw new Error('Failed to change item report status')
    }

    return await response.json()
}
