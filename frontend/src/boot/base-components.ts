import DateInput from 'components/base/DateInput.vue'
import PhoneInput from 'components/base/PhoneInput.vue'
import StyledBtn from 'components/base/StyledBtn.vue'
import StyledCheckbox from 'components/base/StyledCheckbox.vue'
import StyledInput from 'components/base/StyledInput.vue'
import StyledSelect from 'components/base/StyledSelect.vue'
import StyledSpinbox from 'components/base/StyledSpinbox.vue'
import { boot } from 'quasar/wrappers'

// "async" is optional;
// more info on params: https://quasar.dev/quasar-cli/cli-documentation/boot-files#Anatomy-of-a-boot-file
export default boot(({ Vue }) => {
  Vue.component('StyledInput', StyledInput)
  Vue.component('StyledBtn', StyledBtn)
  Vue.component('StyledSelect', StyledSelect)
  Vue.component('StyledSpinbox', StyledSpinbox)
  Vue.component('StyledCheckbox', StyledCheckbox)
  Vue.component('DateInput', DateInput)
  Vue.component('PhoneInput', PhoneInput)
})
