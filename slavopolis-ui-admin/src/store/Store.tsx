import { configureStore } from '@reduxjs/toolkit';
import CustomizerReducer from './customizer/CustomizerSlice.tsx';
import EcommerceReducer from './apps/eCommerce/ECommerceSlice.tsx';
import ChatsReducer from './apps/chat/ChatSlice.tsx';
import NotesReducer from './apps/notes/NotesSlice.tsx';
import EmailReducer from './apps/email/EmailSlice.tsx';
import TicketReducer from './apps/tickets/TicketSlice.tsx';
import ContactsReducer from './apps/contacts/ContactSlice.tsx';
import UserProfileReducer from './apps/userProfile/UserProfileSlice.tsx';
import BlogReducer from './apps/blog/BlogSlice.tsx';
import { combineReducers } from 'redux';
import {
  useDispatch as useAppDispatch,
  useSelector as useAppSelector,
  TypedUseSelectorHook,
} from 'react-redux';

export const store = configureStore({
  reducer: {
    customizer: CustomizerReducer,
    ecommerceReducer: EcommerceReducer,
    chatReducer: ChatsReducer,
    emailReducer: EmailReducer,
    notesReducer: NotesReducer,
    contactsReducer: ContactsReducer,
    ticketReducer: TicketReducer,
    userpostsReducer: UserProfileReducer,
    blogReducer: BlogReducer,
  },
});

const rootReducer = combineReducers({
  customizer: CustomizerReducer,
  ecommerceReducer: EcommerceReducer,
  chatReducer: ChatsReducer,
  emailReducer: EmailReducer,
  notesReducer: NotesReducer,
  contactsReducer: ContactsReducer,
  ticketReducer: TicketReducer,
  userpostsReducer: UserProfileReducer,
  blogReducer: BlogReducer,
});

export type AppState = ReturnType<typeof rootReducer>;
export type AppDispatch = typeof store.dispatch;
export const { dispatch } = store;
export const useDispatch = () => useAppDispatch<AppDispatch>();
export const useSelector: TypedUseSelectorHook<AppState> = useAppSelector;

export default store;
