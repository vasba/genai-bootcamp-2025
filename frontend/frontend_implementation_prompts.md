# Frontend Implementation Prompts

Web can be started locally with: ./gradlew wasmJsBrowserDevelopmentRun

## Phase 1: Project Setup and Navigation
1. Use the boilerplate in frontend/LangPortal to:
   - Set up navigation framework
   - Implement the global navigation bar
   - Set up theme support with dark mode toggle

## Phase 2: Dashboard and Basic Components
1. Implement Dashboard page (/dashboard) with connection to backend api from backend folder
   - Extract the screens in separate files in appropriate folder
   - Create layout for last session summary 
   - Implement student progression display
   - Make it the default route

## Phase 3: Study Activities
1. Implement Study Activities Index (/study-activities)
   - Create activity card component
   - Implement grid layout
   - Add thumbnail, title, and buttons
   
2. Implement Study Activities Show Page (/study-activities/:id)
   - Display activity details (thumbnail, title, description)
   - Create sessions list component
   - Implement launch functionality

## Phase 4: Words Management
1. Implement Words Index Page (/words)
   - Create sortable table component
   - Implement pagination
   - Add sorting indicators
   - Display word statistics (Romanian, English, Correct/Wrong counts)

2. Implement Words Show Page (/words/:id)
   - Create detailed word view
   - Show related statistics

## Phase 5: Word Groups
1. Implement Word Groups Index (/word-groups)
   - Reuse sortable table component
   - Add group statistics
   - Implement pagination

2. Implement Word Groups Show Page (/word-groups/:id)
   - Reuse Words Index components
   - Scope display to group context
   - Add group-specific information

## Phase 6: Sessions and Settings
1. Implement Sessions Index (/sessions)
   - Reuse sortable table component
   - Display session history
   - Implement pagination

2. Implement Settings Page (/settings)
   - Create reset functionality with confirmation dialog
   - Implement dark mode toggle persistence

## Phase 7: Testing and Polish
1. Cross-platform Testing
   - Test on Web
   - Test on Android
   - Test on iOS
   - Ensure consistent behavior

2. Final Polish
   - Implement loading states
   - Add error handling
   - Optimize performance
   - Final UI/UX improvements