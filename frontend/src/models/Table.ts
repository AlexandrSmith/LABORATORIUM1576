export interface Table<T = any> {
  // unique id
  // identifies column
  // (used by pagination.sortBy, "body-cell-[name]" slot, ...)
  name: string

  // label for header
  label: string

  // row Object property to determine value for this column
  field: string | ((row: T) => any)
  // OR field: row => row.some.nested.prop,

  // (optional) if we use visible-columns, this col will always be visible
  required?: boolean
  description?: string
  // (optional) alignment
  align?: 'left' | 'center' | 'right'

  // (optional) tell QTable you want this column sortable
  sortable?: boolean

  // (optional) compare function if you have
  // some custom data or want a specific way to compare two rows
  sort?: (a: any, b: any, rowA: T, rowB: T) => number
  // function return value:
  //   * is less than 0 then sort a to an index lower than b, i.e. a comes first
  //   * is 0 then leave a and b unchanged with respect to each other, but sorted with respect to all different elements
  //   * is greater than 0 then sort b to an index lower than a, i.e. b comes first

  // (optional) you can format the data with a function
  format?: (val: any, row: T) => any
  // one more format example:
  // format: val => val
  //   ? /* Unicode checkmark checked */ "\u2611"
  //   : /* Unicode checkmark unchecked */ "\u2610",

  // body td:
  style?: string | any | string[]
  classes?: string | any | string[]

  // (v1.3+) header th:
  headerStyle?: string
  headerClasses?: string
}

/**
 * Optional function to get a cell value
 * @param col Column name from column definitions
 * @param row The row object
 * @return Parsed/Processed cell value (example: Ice Cream Sandwich)
 */
export type GetCellValueFunction = (col: any, row: any) => any
/**
 * Filter method (the 'filter-method' prop)
 * @param rows Array of rows
 * @param terms Terms to filter with (is essentially the 'filter' prop value)
 * @param cols Optional column definitions
 * @param getCellValue Optional function to get a cell value
 * @return Filtered rows
 */
export type TableFilterFunction = (
  rows: any[],
  terms: string | any,
  cols: Table[],
  getCellValue: GetCellValueFunction
) => any[]

export interface TableRequestProps {
  /**
   * Optional pagination object
   */
  pagination?: {
    /**
     * Column name (from column definition)
     */
    sortBy?: string
    /**
     * Is sorting in descending order?
     */
    descending?: boolean
    /**
     * Page number (1-based)
     */
    page?: number
    /**
     * How many rows per page? 0 means Infinite
     */
    rowsPerPage?: number
  }
  /**
   * Filtering method (the 'filter-method' prop)
   */
  filter?: TableFilterFunction
  getCellValue?: GetCellValueFunction
}
