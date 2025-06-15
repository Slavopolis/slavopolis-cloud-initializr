// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React, { lazy } from 'react';
import { Navigate } from 'react-router-dom';
import Loadable from '../layouts/full/shared/loadable/Loadable.tsx';

/* ***Layouts**** */
const FullLayout = Loadable(lazy(() => import('../layouts/full/FullLayout.tsx')));
const BlankLayout = Loadable(lazy(() => import('../layouts/blank/BlankLayout.tsx')));

/* ****Pages***** */
const ModernDash = Loadable(lazy(() => import('../views/dashboard/Modern.tsx')));
const EcommerceDash = Loadable(lazy(() => import('../views/dashboard/Ecommerce.tsx')));

/* ****Apps***** */
const Blog = Loadable(lazy(() => import('../views/apps/blog/Blog.tsx')));
const BlogDetail = Loadable(lazy(() => import('../views/apps/blog/BlogPost.tsx')));
const Contacts = Loadable(lazy(() => import('../views/apps/contacts/Contacts.tsx')));
const Chats = Loadable(lazy(() => import('../views/apps/chat/Chat.tsx')));
const Notes = Loadable(lazy(() => import('../views/apps/notes/Notes.tsx')));
const Tickets = Loadable(lazy(() => import('../views/apps/tickets/Tickets.tsx')));
const Ecommerce = Loadable(lazy(() => import('../views/apps/eCommerce/Ecommerce.tsx')));
const EcommerceDetail = Loadable(lazy(() => import('../views/apps/eCommerce/EcommerceDetail.tsx')));
const EcomProductList = Loadable(lazy(() => import('../views/apps/eCommerce/EcomProductList.tsx')));
const EcomProductCheckout = Loadable(
  lazy(() => import('../views/apps/eCommerce/EcommerceCheckout.tsx')),
);
const Calendar = Loadable(lazy(() => import('../views/apps/calendar/BigCalendar.tsx')));
const UserProfile = Loadable(lazy(() => import('../views/apps/user-profile/UserProfile.tsx')));
const Followers = Loadable(lazy(() => import('../views/apps/user-profile/Followers.tsx')));
const Friends = Loadable(lazy(() => import('../views/apps/user-profile/Friends.tsx')));
const Gallery = Loadable(lazy(() => import('../views/apps/user-profile/Gallery.tsx')));
const Email = Loadable(lazy(() => import('../views/apps/email/Email.tsx')));

// ui components
const MuiAlert = Loadable(lazy(() => import('../views/ui-components/MuiAlert.tsx')));
const MuiAccordion = Loadable(lazy(() => import('../views/ui-components/MuiAccordion.tsx')));
const MuiAvatar = Loadable(lazy(() => import('../views/ui-components/MuiAvatar.tsx')));
const MuiChip = Loadable(lazy(() => import('../views/ui-components/MuiChip.tsx')));
const MuiDialog = Loadable(lazy(() => import('../views/ui-components/MuiDialog.tsx')));
const MuiList = Loadable(lazy(() => import('../views/ui-components/MuiList.tsx')));
const MuiPopover = Loadable(lazy(() => import('../views/ui-components/MuiPopover.tsx')));
const MuiRating = Loadable(lazy(() => import('../views/ui-components/MuiRating.tsx')));
const MuiTabs = Loadable(lazy(() => import('../views/ui-components/MuiTabs.tsx')));
const MuiTooltip = Loadable(lazy(() => import('../views/ui-components/MuiTooltip.tsx')));
const MuiTransferList = Loadable(lazy(() => import('../views/ui-components/MuiTransferList.tsx')));
const MuiTypography = Loadable(lazy(() => import('../views/ui-components/MuiTypography.tsx')));

// form elements
const MuiAutoComplete = Loadable(
  lazy(() => import('../views/forms/form-elements/MuiAutoComplete.tsx')),
);
const MuiButton = Loadable(lazy(() => import('../views/forms/form-elements/MuiButton.tsx')));
const MuiCheckbox = Loadable(lazy(() => import('../views/forms/form-elements/MuiCheckbox.tsx')));
const MuiRadio = Loadable(lazy(() => import('../views/forms/form-elements/MuiRadio.tsx')));
const MuiSlider = Loadable(lazy(() => import('../views/forms/form-elements/MuiSlider.tsx')));
const MuiDateTime = Loadable(lazy(() => import('../views/forms/form-elements/MuiDateTime.tsx')));
const MuiSwitch = Loadable(lazy(() => import('../views/forms/form-elements/MuiSwitch.tsx')));

// forms
const FormLayouts = Loadable(lazy(() => import('../views/forms/FormLayouts.tsx')));
const FormCustom = Loadable(lazy(() => import('../views/forms/FormCustom.tsx')));
const FormHorizontal = Loadable(lazy(() => import('../views/forms/FormHorizontal.tsx')));
const FormVertical = Loadable(lazy(() => import('../views/forms/FormVertical.tsx')));
const FormWizard = Loadable(lazy(() => import('../views/forms/FormWizard.tsx')));
const FormValidation = Loadable(lazy(() => import('../views/forms/FormValidation.tsx')));
const QuillEditor = Loadable(lazy(() => import('../views/forms/quill-editor/QuillEditor.tsx')));

// pages
const RollbaseCASL = Loadable(lazy(() => import('../views/pages/rollbaseCASL/RollbaseCASL.tsx')));
const Treeview = Loadable(lazy(() => import('../views/pages/treeview/Treeview.tsx')));
const Faq = Loadable(lazy(() => import('../views/pages/faq/Faq.tsx')));
const Pricing = Loadable(lazy(() => import('../views/pages/pricing/Pricing.tsx')));
const AccountSetting = Loadable(
  lazy(() => import('../views/pages/account-setting/AccountSetting.tsx')),
);

// charts
const AreaChart = Loadable(lazy(() => import('../views/charts/AreaChart.tsx')));
const CandlestickChart = Loadable(lazy(() => import('../views/charts/CandlestickChart.tsx')));
const ColumnChart = Loadable(lazy(() => import('../views/charts/ColumnChart.tsx')));
const DoughnutChart = Loadable(lazy(() => import('../views/charts/DoughnutChart.tsx')));
const GredientChart = Loadable(lazy(() => import('../views/charts/GredientChart.tsx')));
const RadialbarChart = Loadable(lazy(() => import('../views/charts/RadialbarChart.tsx')));
const LineChart = Loadable(lazy(() => import('../views/charts/LineChart.tsx')));

// tables
const BasicTable = Loadable(lazy(() => import('../views/tables/BasicTable.tsx')));
const EnhanceTable = Loadable(lazy(() => import('../views/tables/EnhanceTable.tsx')));
const PaginationTable = Loadable(lazy(() => import('../views/tables/PaginationTable.tsx')));
const FixedHeaderTable = Loadable(lazy(() => import('../views/tables/FixedHeaderTable.tsx')));
const CollapsibleTable = Loadable(lazy(() => import('../views/tables/CollapsibleTable.tsx')));
const SearchTable = Loadable(lazy(() => import('../views/tables/SearchTable.tsx')));

// widget
const WidgetCards = Loadable(lazy(() => import('../views/widgets/cards/WidgetCards.tsx')));
const WidgetBanners = Loadable(lazy(() => import('../views/widgets/banners/WidgetBanners.tsx')));
const WidgetCharts = Loadable(lazy(() => import('../views/widgets/charts/WidgetCharts.tsx')));

// authentication
const Login = Loadable(lazy(() => import('../views/authentication/auth1/Login.tsx')));
const Login2 = Loadable(lazy(() => import('../views/authentication/auth2/Login2.tsx')));
const Register = Loadable(lazy(() => import('../views/authentication/auth1/Register.tsx')));
const Register2 = Loadable(lazy(() => import('../views/authentication/auth2/Register2.tsx')));
const ForgotPassword = Loadable(lazy(() => import('../views/authentication/auth1/ForgotPassword.tsx')));
const ForgotPassword2 = Loadable(
  lazy(() => import('../views/authentication/auth2/ForgotPassword2.tsx')),
);
const TwoSteps = Loadable(lazy(() => import('../views/authentication/auth1/TwoSteps.tsx')));
const TwoSteps2 = Loadable(lazy(() => import('../views/authentication/auth2/TwoSteps2.tsx')));
const Error = Loadable(lazy(() => import('../views/authentication/Error.tsx')));
const Maintenance = Loadable(lazy(() => import('../views/authentication/Maintenance.tsx')));

// landingpage
const Landingpage = Loadable(lazy(() => import('../views/pages/landingpage/Landingpage.tsx')));

const Router = [
  {
    path: '/',
    element: <FullLayout />,
    children: [
      { path: '/', element: <Navigate to="/dashboards/modern" /> },
      { path: '/dashboards/modern', exact: true, element: <ModernDash /> },
      { path: '/dashboards/ecommerce', exact: true, element: <EcommerceDash /> },
      { path: '/apps/contacts', element: <Contacts /> },
      { path: '/apps/blog/posts', element: <Blog /> },
      { path: '/apps/blog/detail/:id', element: <BlogDetail /> },
      { path: '/apps/chats', element: <Chats /> },
      { path: '/apps/email', element: <Email /> },
      { path: '/apps/notes', element: <Notes /> },
      { path: '/apps/tickets', element: <Tickets /> },
      { path: '/apps/ecommerce/shop', element: <Ecommerce /> },
      { path: '/apps/ecommerce/eco-product-list', element: <EcomProductList /> },
      { path: '/apps/ecommerce/eco-checkout', element: <EcomProductCheckout /> },
      { path: '/apps/ecommerce/detail/:id', element: <EcommerceDetail /> },
      { path: '/apps/followers', element: <Followers /> },
      { path: '/apps/friends', element: <Friends /> },
      { path: '/apps/gallery', element: <Gallery /> },
      { path: '/user-profile', element: <UserProfile /> },
      { path: '/apps/calendar', element: <Calendar /> },
      { path: '/ui-components/alert', element: <MuiAlert /> },
      { path: '/ui-components/accordion', element: <MuiAccordion /> },
      { path: '/ui-components/avatar', element: <MuiAvatar /> },
      { path: '/ui-components/chip', element: <MuiChip /> },
      { path: '/ui-components/dialog', element: <MuiDialog /> },
      { path: '/ui-components/list', element: <MuiList /> },
      { path: '/ui-components/popover', element: <MuiPopover /> },
      { path: '/ui-components/rating', element: <MuiRating /> },
      { path: '/ui-components/tabs', element: <MuiTabs /> },
      { path: '/ui-components/tooltip', element: <MuiTooltip /> },
      { path: '/ui-components/transfer-list', element: <MuiTransferList /> },
      { path: '/ui-components/typography', element: <MuiTypography /> },
      { path: '/pages/casl', element: <RollbaseCASL /> },
      { path: '/pages/treeview', element: <Treeview /> },
      { path: '/pages/pricing', element: <Pricing /> },
      { path: '/pages/faq', element: <Faq /> },
      { path: '/pages/account-settings', element: <AccountSetting /> },
      { path: '/tables/basic', element: <BasicTable /> },
      { path: '/tables/enhanced', element: <EnhanceTable /> },
      { path: '/tables/pagination', element: <PaginationTable /> },
      { path: '/tables/fixed-header', element: <FixedHeaderTable /> },
      { path: '/tables/collapsible', element: <CollapsibleTable /> },
      { path: '/tables/search', element: <SearchTable /> },
      { path: '/forms/form-elements/autocomplete', element: <MuiAutoComplete /> },
      { path: '/forms/form-elements/button', element: <MuiButton /> },
      { path: '/forms/form-elements/checkbox', element: <MuiCheckbox /> },
      { path: '/forms/form-elements/radio', element: <MuiRadio /> },
      { path: '/forms/form-elements/slider', element: <MuiSlider /> },
      { path: '/forms/form-elements/date-time', element: <MuiDateTime /> },
      { path: '/forms/form-elements/switch', element: <MuiSwitch /> },
      { path: '/forms/form-elements/switch', element: <MuiSwitch /> },
      { path: '/forms/form-layouts', element: <FormLayouts /> },
      { path: '/forms/form-custom', element: <FormCustom /> },
      { path: '/forms/form-wizard', element: <FormWizard /> },
      { path: '/forms/form-validation', element: <FormValidation /> },
      { path: '/forms/form-horizontal', element: <FormHorizontal /> },
      { path: '/forms/form-vertical', element: <FormVertical /> },
      { path: '/forms/quill-editor', element: <QuillEditor /> },
      { path: '/charts/area-chart', element: <AreaChart /> },
      { path: '/charts/line-chart', element: <LineChart /> },
      { path: '/charts/gredient-chart', element: <GredientChart /> },
      { path: '/charts/candlestick-chart', element: <CandlestickChart /> },
      { path: '/charts/column-chart', element: <ColumnChart /> },
      { path: '/charts/doughnut-pie-chart', element: <DoughnutChart /> },
      { path: '/charts/radialbar-chart', element: <RadialbarChart /> },
      { path: '/widgets/cards', element: <WidgetCards /> },
      { path: '/widgets/banners', element: <WidgetBanners /> },
      { path: '/widgets/charts', element: <WidgetCharts /> },
      { path: '*', element: <Navigate to="/auth/404" /> },
    ],
  },
  {
    path: '/',
    element: <BlankLayout />,
    children: [
      { path: '/auth/404', element: <Error /> },
      { path: '/auth/login', element: <Login /> },
      { path: '/auth/login2', element: <Login2 /> },
      { path: '/auth/register', element: <Register /> },
      { path: '/auth/register2', element: <Register2 /> },
      { path: '/auth/forgot-password', element: <ForgotPassword /> },
      { path: '/auth/forgot-password2', element: <ForgotPassword2 /> },
      { path: '/auth/two-steps', element: <TwoSteps /> },
      { path: '/auth/two-steps2', element: <TwoSteps2 /> },
      { path: '/auth/maintenance', element: <Maintenance /> },
      { path: '/landingpage', element: <Landingpage /> },
      { path: '*', element: <Navigate to="/auth/404" /> },
    ],
  },
];

export default Router;
