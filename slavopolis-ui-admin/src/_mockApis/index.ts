import mock from './mock.ts';
import './blog/blogData.ts';
import './contacts/ContactsData.tsx';
import './chat/Chatdata.ts';
import './notes/NotesData.ts';
import './ticket/TicketData.ts';
import './eCommerce/ProductsData.ts';
import './email/EmailData.tsx';
import './userprofile/PostData.ts';
import './userprofile/UsersData.ts';

mock.onAny().passThrough();
