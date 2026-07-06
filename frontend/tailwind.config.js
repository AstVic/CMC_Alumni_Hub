/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        // Deep blue — primary brand colour (university/tech portal feel).
        brand: {
          50: '#eef4ff',
          100: '#d9e5ff',
          200: '#bcd1ff',
          300: '#8eb3ff',
          400: '#598bff',
          500: '#3363f5',
          600: '#1f47db',
          700: '#1a37b0',
          800: '#1b3190',
          900: '#1c2e73',
          950: '#141d47',
        },
        // Accent — blue-violet used for highlights and CTAs.
        accent: {
          400: '#8b7dff',
          500: '#6d5cf5',
          600: '#5a45e0',
        },
        // Neutral light-gray surface palette.
        surface: {
          DEFAULT: '#ffffff',
          muted: '#f5f7fa',
          border: '#e4e8ef',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      boxShadow: {
        card: '0 1px 3px rgba(20, 29, 71, 0.06), 0 8px 24px rgba(20, 29, 71, 0.06)',
      },
      borderRadius: {
        xl: '0.875rem',
        '2xl': '1.25rem',
      },
    },
  },
  plugins: [],
};
