import { Action, getModule, Module, Mutation, VuexModule } from 'vuex-module-decorators'
import { ProjectInfoDto } from 'src/models/Project'
import { handleApiError } from 'src/utils/errorHandler'
import store from 'src/store'
import checkIfInStore from 'src/store/utils'
import ProjectApi from 'src/api/ProjectApi'

@Module({ name: 'menu', store, dynamic: true, namespaced: true, preserveState: checkIfInStore('menu') })
class MenuModule extends VuexModule {
  projects: ProjectInfoDto[] = []

  get projectList() {
    return this.projects
  }

  @Action
  async fetchProjects() {
    try {
      const list = await ProjectApi.list()
      if (list) this.SET_PROJECTS(list)
    } catch (e) {
      handleApiError(e)
    }
  }

  @Mutation
  SET_PROJECTS(projects: ProjectInfoDto[]) {
    this.projects = projects
  }
}

const MenuStore = getModule(MenuModule)
export default MenuStore
