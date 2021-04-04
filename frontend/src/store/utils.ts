export const vuexKey = 'store_v0.1'

export default function checkIfInStore(name: string) {
  const vuex = window.localStorage.getItem(vuexKey)
  if (vuex != null) {
    try {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      const parsed: any = JSON.parse(vuex)
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      return name in parsed && parsed[name] != null
    } catch (e) {
      return false
    }
  }
  return false
}
