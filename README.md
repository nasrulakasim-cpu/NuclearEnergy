# Daily Task Tracker

A simple, static web app for tracking your daily tasks — add tasks, set a
priority, check them off, and watch your daily progress bar fill up.

## Features

- Add tasks with a priority (low / medium / high)
- Mark tasks complete/incomplete
- Delete individual tasks or clear all completed tasks at once
- Filter by All / Active / Completed
- Live progress bar showing how much of today's list is done
- Tasks persist in your browser via `localStorage` — no backend required

## Running it

This is a static site — no build step or server needed. Just open
`index.html` in a browser, or serve the folder with any static file server:

```bash
python3 -m http.server 8000
# then visit http://localhost:8000
```

## Files

- `index.html` — page structure
- `style.css` — styling
- `script.js` — task logic (add/toggle/delete/filter, localStorage persistence)
