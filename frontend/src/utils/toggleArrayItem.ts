export default function toggleArrayItem(array: unknown[], item: unknown) {
  const index = array.indexOf(item)
  if (index === -1) {
    array.push(item)
  } else {
    array.splice(index, 1)
  }
}
