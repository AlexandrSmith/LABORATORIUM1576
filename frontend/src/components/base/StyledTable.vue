<template>
  <q-table
    flat
    v-bind="$attrs"
    :data="data"
    :columns="internalColumns"
    :class="qTableClass"
    :table-class="tableClass"
    :row-key="rowKey"
    :loading="loading"
    table-header-class="styled-table__header"
    :pagination="pagination"
    :rows-per-page-options="rowsPerPageOptions"
    :hide-bottom="hideBottom"
    :filter="filter"
    :filter-method="filterMethod"
    :virtual-scroll="virtualScroll"
    :virtual-scroll-sticky-size-start="headerHeight"
    :wrap-cells="wrapCells"
    :selection="selection"
    :selected="selected"
    @row-click="onRowClick"
    @virtual-scroll="$emit('virtual-scroll', $event)"
    @request="$emit('request', $event)"
    @update:selected="$emit('update:selected', $event)"
  >
    <template #loading>
      <q-inner-loading class="styled-table__loading" :showing="loading">
        <q-spinner-hourglass size="64px" />
      </q-inner-loading>
    </template>

    <template #top-right="props">
      <slot name="top-right" v-bind="props" />
    </template>

    <template #header-cell-__checkboxes="props">
      <q-th style="padding-left: 8px" v-bind="props">
        <slot name="header-checkbox" v-bind="props" />
      </q-th>
    </template>

    <template #body-cell-__actions="props">
      <q-td v-bind="props">
        <slot name="row-actions" v-bind="props" />
      </q-td>
    </template>

    <template #body-cell-image="props">
      <q-td key="props.row.image" v-bind="props">
        <q-img
          contain
          height="var(--table-image-size, 100px)"
          width="var(--table-image-size, 100px)"
          :src="
            props.row.image
              ? typeof props.row.image === 'object'
                ? URL.createObjectURL(props.row.image)
                : props.row.image
              : null
          "
        />
      </q-td>
    </template>

    <template #no-data>
      <div class="flex column no-wrap full-width col-grow">
        <div class="styled-table__no-data col-grow items-center justify-center flex">
          {{ noDataLabel || 'Тут ничего нет...' }}
        </div>
      </div>
    </template>

    <!--suppress JSUnusedLocalSymbols -->
    <template v-for="(_, name) in $scopedSlots" :slot="name" slot-scope="slotData">
      <slot :name="name" v-bind="slotData" />
    </template>
  </q-table>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, reactive } from '@vue/composition-api'
import { mergeDeepRight } from 'ramda'
import { Table } from 'src/models/Table'
import detectVerticalOverflow from 'src/utils/detectVerticalOverflow'

export default defineComponent({
  name: 'StyledTable',
  props: {
    data: {
      type: Array,
      default: () => [],
    },
    rowKey: {
      type: String,
      default: 'id',
    },
    noDataLabel: {
      type: String,
      default: undefined,
    },
    columns: {
      type: Array as PropType<Table[]>,
      required: true,
    },
    tableClass: {
      type: [Array, Object, String],
      default: undefined,
    },
    disableNthBackground: {
      type: Boolean,
      default: false,
    },
    filter: {
      type: [String, Object],
      default: undefined,
    },
    filterMethod: {
      type: Function,
      default: undefined,
    },
    virtualScroll: {
      type: Boolean,
      default: false,
    },
    wrapCells: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
    },
    selection: {
      type: String as PropType<'none' | 'single' | 'multiple'>,
      default: 'none',
    },
    selected: {
      type: Array,
      default: undefined,
    },
    hideBottom: {
      type: Boolean,
      default: false,
    },

    // events
    // Костыль для автодополнения событий
    /* eslint-disable vue/prop-name-casing, vue/require-default-prop */
    /**
     * Emitted when user clicks/taps on a row; Is not emitted when using body/row/item scoped slots
     * @param {Object} row The row upon which user has clicked/tapped
     */
    '@rowClick': Function,
    /**
     * Emitted when a server request is triggered
     * @param {{pagination : {sortBy : String, descending : Boolean, page : Number, rowsPerPage : Number}, filter : Function, getCellValue : Function}} requestProp Props of the request
     */
    '@request': Function,
    /**
     * Emitted when the virtual scroll occurs, if using virtual scroll
     * @param {{index : Number, from : Number, to : Number, direction : 'increase'|'decrease'}} details Object of properties on the new scroll position
     */
    '@virtualScroll': Function,
    /* eslint-enable vue/prop-name-casing, vue/require-default-prop */
  },
  setup(props, ctx) {
    const noData = computed(() => props.data.length === 0)

    const { isOverflowed } = detectVerticalOverflow(() => props.data, '.q-table__middle', ctx)
    const qTableClass = computed(() => ({
      'styled-table': true,
      'styled-table--overflowed': isOverflowed.value,
      'styled-table--no-data': noData.value,
      'styled-table--nth-background': !props.disableNthBackground,
    }))
    const pagination = reactive({
      rowsPerPage: 0,
    })
    const rowsPerPageOptions = [0]

    const defaultColumnOptions: Partial<Table> = {
      align: 'left',
    }
    const internalColumns = computed(() => props.columns.map((col) => mergeDeepRight(defaultColumnOptions, col)))

    // const hideBottom = !('bottom' in ctx.slots);

    function onRowClick(e: any, payload: any[]) {
      ctx.emit('row-click', payload)
    }

    const headerHeight = 40

    function debug(payload: any) {
      console.log(payload)
      return 'none'
    }

    return {
      internalColumns,

      qTableClass,
      pagination,
      rowsPerPageOptions,
      // hideBottom,
      noData,

      debug,

      headerHeight,

      onRowClick,
    }
  },
})
</script>

<style lang="scss">
$table-header-height: 40px;
$table-header-color: #f2f2f2;
$table-header-background: #0c2838;

$table-background: #ffffff;
$table-bottom-background: #f4ebdf;
$table-bottom-border-color: #bdbdbd;

$table-row-primary-background: #ffffff;
$table-row-secondary-background: #efefef;

$table-title-font-size: 13px;
$table-font-size: 13px;

.styled-table {
  contain: content;

  border-top-left-radius: 0;
  border-top-right-radius: 0;

  &--bottom-hidden#{&} {
    table {
      border-bottom: none;
    }
  }

  table {
    border-bottom: 2px solid $table-bottom-border-color;
  }

  thead th {
    font-size: $table-font-size;
    //text-align: left;
    font-weight: bold;
    padding: 4px 8px;
  }

  tbody td {
    font-size: $table-font-size;
    padding: 4px 8px;
  }

  thead > tr {
    height: $table-header-height;
  }

  tbody > tr:last-child > td {
    /*border-bottom: 1px groove rgba(0, 0, 0, 0.12);*/
  }

  thead > tr:first-child > th {
    color: $table-header-color;
    /*border-bottom: 1px solid #27ae60;*/
    background: $table-header-background;
  }

  thead tr:first-child th {
    top: 0;
  }
  //27AE60
  thead > tr > th {
    font-weight: normal;
    position: sticky;
    z-index: 1;
  }

  &__loading {
    z-index: 2;
  }

  thead > tr > th:first-of-type .q-checkbox .q-checkbox__inner,
  tbody > tr > td:first-of-type .q-checkbox .q-checkbox__inner {
    color: $primary !important;
  }

  tbody {
    background: $table-background;
  }

  &--nth-background {
    tbody > tr:nth-child(odd) {
      background: $table-row-primary-background;
    }

    tbody > tr:nth-child(even) {
      background: $table-row-secondary-background;
    }
  }

  &__no-data {
    font-size: 2rem !important;
    background: $table-background;
  }

  &--no-data,
  &--overflowed {
    table {
      border-bottom: none;
    }
  }

  &--no-data {
    > .q-table__middle {
      flex: 0 1 auto;
    }

    > .q-table__bottom {
      flex-direction: column;
      flex: 1 1 auto;
      padding: 0;
      border-top: none;
    }
  }
}

.q-table {
  &__top {
    padding: 0 0 4px;
  }

  &__title {
    font-size: $table-title-font-size;
  }

  &__middle {
    background: $table-background;
  }

  &__bottom {
    background: $table-bottom-background;
    font-size: $table-font-size;

    .q-btn {
      font-size: $table-font-size;
    }

    border-top: 2px solid $table-bottom-border-color;
    padding: 4px 4px;
  }
}

.table-block {
  max-width: 0;
  font-size: 18px;
  color: #4f4f4f;
}
</style>
