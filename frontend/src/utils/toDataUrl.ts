export default function toDataUrl(file: File | Blob): Promise<string | null> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result ? reader.result.toString() : null)
    reader.onerror = (error) => reject(error)
    reader.readAsDataURL(file)
  })
}
