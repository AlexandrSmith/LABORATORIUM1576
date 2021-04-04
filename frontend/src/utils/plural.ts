/**
 * Получение формы множественного числа
 * @param n число
 * @param titles [для 1, для 2-4, для > 5] (пример ['год', 'года', 'лет'])
 * @return форма множественного числа
 */
export default function plural(n: number, titles: string[]) {
  if (n % 10 === 1 && n % 100 !== 11) {
    return titles[0]
  }
  if (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20)) {
    return titles[1]
  }
  return titles[2]
}
