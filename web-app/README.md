# Python EAD Learning Platform - Web App

Modern web application for displaying Python learning content with markdown support.

## Tech Stack

- **Next.js 14** - React framework with App Router
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **gray-matter** - Frontmatter parsing
- **remark/rehype** - Markdown rendering with syntax highlighting

## Getting Started

### Prerequisites

- Node.js 18+ installed
- npm or yarn package manager

### Installation

1. Install dependencies:

```bash
cd web-app
npm install
```

2. Run the development server:

```bash
npm run dev
```

3. Open [http://localhost:3000](http://localhost:3000) in your browser.

## Project Structure

```
web-app/
├── src/
│   ├── app/                    # Next.js App Router pages
│   │   ├── layout.tsx          # Root layout with header/footer
│   │   ├── page.tsx            # Home page
│   │   ├── globals.css         # Global styles
│   │   └── modules/            # Module pages
│   │       ├── page.tsx        # Modules index
│   │       └── [module]/       # Dynamic module pages
│   │           ├── page.tsx    # Module detail
│   │           └── [unit]/     # Dynamic unit pages
│   │               └── page.tsx # Unit detail (Knowledge, Exercises, Labs)
│   └── lib/
│       ├── content.ts          # Content fetching utilities
│       └── markdown.ts         # Markdown to HTML conversion
├── public/                     # Static assets
├── package.json
├── tsconfig.json
├── tailwind.config.js
└── next.config.js
```

## Features

### ✅ Implemented

- **Markdown Rendering**: Full support for GFM (tables, code blocks, etc.)
- **Syntax Highlighting**: Code blocks with syntax highlighting
- **Frontmatter Parsing**: YAML frontmatter for metadata
- **Responsive Design**: Mobile-friendly layout
- **Dark Mode Support**: Automatic dark mode based on system preferences
- **Collapsible Hints**: Progressive disclosure for exercise hints
- **Tab Navigation**: Knowledge, Exercises, Labs, Learning Outcomes

### Content Structure

The app reads content from `../content/` directory:

```
content/
├── modules/
│   └── collections/
│       └── unit_1_lists/
│           ├── knowledge/
│           │   └── lesson_01.md
│           ├── exercises/
│           │   └── README.md
│           ├── app_labs/
│           │   └── lab_1_easy/
│           │       └── README.md
│           └── LEARNING_OUTCOMES.md
```

## Development

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm run lint` - Run ESLint

### Adding New Content

1. Add markdown files to `../content/modules/[module]/[unit]/`
2. Include YAML frontmatter at the top:

```yaml
---
title: "Your Title"
type: knowledge
module: collections
unit: unit_1_lists
order: 1
difficulty: easy
tags:
  topics: ["lists"]
  subtopics:
    - indexing
    - slicing
---
```

3. Content will automatically appear in the web app

## Deployment

### Vercel (Recommended)

1. Push code to GitHub
2. Import project in Vercel
3. Deploy automatically

### Other Platforms

```bash
npm run build
npm start
```

## Troubleshooting

### Content Not Showing

- Verify `../content/` directory exists relative to `web-app/`
- Check YAML frontmatter syntax in markdown files
- Check browser console for errors

### Styling Issues

- Clear `.next` cache: `rm -rf .next`
- Reinstall dependencies: `rm -rf node_modules && npm install`

## Future Enhancements

- [ ] Search functionality
- [ ] Progress tracking
- [ ] Interactive code execution
- [ ] User authentication
- [ ] Analytics dashboard
- [ ] PDF export
