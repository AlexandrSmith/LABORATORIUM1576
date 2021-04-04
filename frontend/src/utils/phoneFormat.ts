export default function formatPhoneNumber(phoneString: string | undefined) {
  if (phoneString == null) {
    return null
  }
  return `+7 (${phoneString.substr(0, 3)}) ${phoneString.substr(3, 3)}-${phoneString.substr(6, 2)}-${phoneString.substr(
    8,
    3
  )}`
}
