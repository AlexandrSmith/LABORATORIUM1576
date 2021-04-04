export default function mixedStringSort(a: string, b: string) {
  return new Intl.Collator(['ru', 'en'], {
    numeric: true,
    sensitivity: 'accent',
  }).compare(a, b)
}
