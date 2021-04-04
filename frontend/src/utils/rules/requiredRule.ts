export default function requiredRule(val: any, msg = 'Небходимо ввести значение'): boolean | string {
  return !!val || msg
}
