import { combineReducers } from 'redux'
import reducers from './reducers'

// TODO: are we shoulkd we need the redux here? could we maybe consider something smaller? eg: RTK, zustand or some kind of their alternatives?
export default combineReducers({
  session: reducers
})
