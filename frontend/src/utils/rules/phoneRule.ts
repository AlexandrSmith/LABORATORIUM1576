export default function phoneRule(value: string) {
  const regex = /(\+7 )?\(?\d{3}\)?\s?\d{3}-?\d{2}-?\d{2}/
  return regex.test(value) || 'Некорректный номер'
}
