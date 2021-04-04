export default function makeSrcSet(url: string, sizes: number[]): string {
  return sizes.map((size) => `${imageAtSize(url, size)} ${size}w`).join(', ')
}

export function imageAtSize(url: string, size: number): string {
  return url.replace('/files/', `/files/${size}/`)
}
