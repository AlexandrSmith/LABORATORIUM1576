<template>
  <bordered-text-block done required without-question>
    <template #title>
      <div class="text-weight-bold" style="margin-left: 0">{{ project.name }}</div>
      <!--    <div>Тема проекта: {{ project.researchTopic }}</div>-->
      <div>Предметная область: {{ project.subjectAreas.map((x) => x.title).join('; ') }}</div>
      <div>ФИО ученика: {{ project.learner.fullName }}</div>
      <div>Дата начала проекта: {{ isoToLocalDate(project.createDate) }}</div>
    </template>
  </bordered-text-block>
</template>

<script lang="ts">
import { defineComponent, PropType } from '@vue/composition-api'
import { ProjectDto } from 'src/models/Project'
import { isoToLocalDate } from 'src/utils/isoToLocalDate'
import BorderedTextBlock from 'components/base/BorderedTextBlock.vue'

export default defineComponent({
  name: 'TeacherProjectTitle',
  components: { BorderedTextBlock },
  props: {
    project: {
      type: Object as PropType<ProjectDto>,
      default: null,
    },
  },
  setup() {
    return {
      isoToLocalDate,
    }
  },
})
</script>

<style lang="scss" scoped>
.green-link {
  font-size: 14px;
  color: #219653;
}
</style>
