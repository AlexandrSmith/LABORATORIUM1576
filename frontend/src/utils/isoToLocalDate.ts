import { formatISO, isValid, lightFormat, parse, parseISO } from 'date-fns/fp'
import { always, ifElse, pipe } from 'ramda'

export const localFormat = 'dd.MM.yyyy'
export const localFormatDateTime = 'dd.MM.yyyy HH:mm'
export const localFormatTime = 'HH:mm'

const empty = always('')

export function isoToLocalDate(x: string): string {
  return pipe<string, Date, string>(parseISO, ifElse(isValid, lightFormat(localFormat), empty))(x)
}

export function isoToLocalDateTime(x: string): string {
  return pipe<string, Date, string>(parseISO, ifElse(isValid, lightFormat(localFormatDateTime), empty))(x)
}

export function isoToLocalTime(x: string) {
  return pipe<string, Date, string>(parseISO, ifElse(isValid, lightFormat(localFormatTime), empty))(x)
}

export function localDateToIso(date: string | null | undefined): string | undefined {
  if (date == null || date === '') {
    return undefined
  }
  return pipe(parse(new Date(), localFormat), lightFormat('yyyy-MM-dd'))(date)
}

export function localDateTimeToIso(date: string | undefined): string | undefined {
  if (date == null || date === '') {
    return undefined
  }
  return pipe(parse(new Date(), localFormatDateTime), formatISO)(date)
}
