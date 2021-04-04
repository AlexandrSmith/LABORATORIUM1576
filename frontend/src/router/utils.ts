import { combineURLs } from 'src/api-client/utils'
import { Route, RouteConfig } from 'vue-router'

export interface RouteLink {
  label: string
  name?: string
  path?: string
  exact?: boolean
  params?: Record<string, unknown>
}

export interface RouteConfigExt extends RouteConfig {
  meta?: {
    label?: string
    auth?: boolean
    skipLink?: boolean
    exact?: boolean
  }
}

export const parsePropToInt = (prop: string) => (route: Route) => ({
  ...route.params,
  [prop]: parseInt(route.params[prop]) || null,
})

export const parsePropsToInt = (props: string[]) => (route: Route) => ({
  ...route.params,
  ...props.reduce(
    (acc, prop) => ({
      ...acc,
      [prop]: parseInt(route.params[prop]) || null,
    }),
    {}
  ),
})

export function getRouteLinks(route: RouteConfigExt, maxDepth = 1): RouteLink[] {
  const routeLinks: RouteLink[] = []

  function walk(route: RouteConfigExt, parentPath = '', depth = 0) {
    const path = combineURLs(parentPath, route.path)
    if (!route.meta?.skipLink) {
      routeLinks.push({
        name: route.name,
        path,
        label: route.meta?.label || 'unknown',
        exact: route.meta?.exact,
      })
    }
    if (depth === maxDepth) {
      return
    }
    route.children?.forEach((x) => walk(x, path, depth + 1))
  }

  walk(route)
  return routeLinks
}
