interface ApiErrorBody {
  message?: string
  details?: string[]
}

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${import.meta.env.VITE_API_BASE_URL ?? ''}${path}`, init)
  if (!response.ok) {
    const body = (await response.json().catch(() => ({}))) as ApiErrorBody
    const details = body.details?.length ? ` ${body.details.join(' ')}` : ''
    throw new Error(`${body.message ?? '요청을 처리하지 못했습니다.'}${details}`)
  }
  return response.json() as Promise<T>
}
