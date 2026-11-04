
const API_BASE_URL = 'http://localhost:8080'

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

    const items = await response.json()

    return items
}

export async function getItemById(id) {
    const items = await getAllItems()

    const item = items.find((item) => String(item.id) === String(id))

    if (!item) {
        throw new Error('Item not found')
    }

    return item
}

export async function createItemReport(itemData) {
    const csrfResponse = await fetch(`${API_BASE_URL}/auth/csrf`, {
        method: 'GET',
        credentials: 'include'
    })

    if (!csrfResponse.ok) {
        throw new Error('Failed to fetch CSRF token')
    }

    const { token, headerName } = await csrfResponse.json()

    const response = await fetch(`${API_BASE_URL}/item-reports`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            [headerName]: token
        },
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

    return await response.json()
}
