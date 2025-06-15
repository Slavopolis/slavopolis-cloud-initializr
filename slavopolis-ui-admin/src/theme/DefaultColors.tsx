const baselightTheme = {
  direction: 'ltr',
  palette: {
    primary: {
      main: '#5D87FF',
      light: '#ECF2FF',
      dark: '#4570EA',
    },
    secondary: {
      main: '#49BEFF',
      light: '#E8F7FF',
      dark: '#23afdb',
    },
    success: {
      main: '#13DEB9',
      light: '#E6FFFA',
      dark: '#02b3a9',
      contrastText: '#ffffff',
    },
    info: {
      main: '#539BFF',
      light: '#EBF3FE',
      dark: '#1682d4',
      contrastText: '#ffffff',
    },
    error: {
      main: '#FA896B',
      light: '#FDEDE8',
      dark: '#f3704d',
      contrastText: '#ffffff',
    },
    warning: {
      main: '#FFAE1F',
      light: '#FEF5E5',
      dark: '#ae8e59',
      contrastText: '#ffffff',
    },
    purple: {
      A50: '#EBF3FE',
      A100: '#6610f2',
      A200: '#557fb9',
    },
    grey: {
      100: '#F2F6FA',
      200: '#EAEFF4',
      300: '#DFE5EF',
      400: '#7C8FAC',
      500: '#5A6A85',
      600: '#2A3547',
    },
    text: {
      primary: '#2A3547',
      secondary: '#2A3547',
    },
    action: {
      disabledBackground: 'rgba(73,82,88,0.12)',
      hoverOpacity: 0.02,
      hover: '#f6f9fc',
    },
    divider: '#e5eaef',
    background: {
      default: '#ffffff',
    },
  },
};

const baseDarkTheme = {
  direction: 'ltr',
  palette: {
    primary: {
      main: '#6366f1',
      light: '#a5b4fc',
      dark: '#4f46e5',
    },
    secondary: {
      main: '#8b5cf6',
      light: '#c4b5fd',
      dark: '#7c3aed',
    },
    success: {
      main: '#10b981',
      light: '#6ee7b7',
      dark: '#059669',
      contrastText: '#ffffff',
    },
    info: {
      main: '#3b82f6',
      light: '#93c5fd',
      dark: '#2563eb',
      contrastText: '#ffffff',
    },
    error: {
      main: '#f87171',
      light: '#fca5a5',
      dark: '#dc2626',
      contrastText: '#ffffff',
    },
    warning: {
      main: '#f59e0b',
      light: '#fbbf24',
      dark: '#d97706',
      contrastText: '#ffffff',
    },
    purple: {
      A50: '#f3f4f6',
      A100: '#8b5cf6',
      A200: '#6d28d9',
    },
    grey: {
      100: '#374151',
      200: '#4b5563',
      300: '#6b7280',
      400: '#9ca3af',
      500: '#d1d5db',
      600: '#e5e7eb',
    },
    text: {
      primary: '#f9fafb',
      secondary: '#d1d5db',
    },
    action: {
      disabledBackground: 'rgba(107,114,128,0.12)',
      hoverOpacity: 0.08,
      hover: '#374151',
    },
    divider: '#374151',
    background: {
      default: '#111827',
      dark: '#0f172a',
      paper: '#1f2937',
    },
  },
};

export { baseDarkTheme, baselightTheme };

