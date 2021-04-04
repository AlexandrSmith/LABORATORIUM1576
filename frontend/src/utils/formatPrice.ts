export default function formatPrice(price: number | null | undefined): string {
  if (price == null) {
    return ''
  }
  const options = {
    style: 'currency',
    currency: 'RUB',
    currencyDisplay: 'symbol',
    minimumFractionDigits: 2,
  }
  if (price % 1 === 0) {
    options.minimumFractionDigits = 0
  }
  return price?.toLocaleString('ru', options)
}
