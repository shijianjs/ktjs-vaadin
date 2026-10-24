// Minimal static server for the production bundle. Routes live in the fragment (`#/button`), so no
// single-page fallback is needed: every request maps to a real file.
import { readFile } from 'node:fs/promises'
import { createServer } from 'node:http'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const here = path.dirname(fileURLToPath(import.meta.url))
const distDir = path.resolve(process.env.DEMO_DIST || path.join(here, '../../build/dist/js/productionExecutable'))
const port = Number(process.env.PORT ?? 4173)

const mimeTypes = {
  '.css': 'text/css; charset=utf-8',
  '.html': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.map': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.svg': 'image/svg+xml',
  '.woff2': 'font/woff2',
}

const server = createServer(async (request, response) => {
  const pathname = decodeURIComponent(new URL(request.url ?? '/', 'http://127.0.0.1').pathname)
  const relative = pathname === '/' ? 'index.html' : pathname.replace(/^\/+/, '')
  const filePath = path.join(distDir, relative)

  if (!filePath.startsWith(distDir + path.sep)) {
    response.writeHead(403).end()
    return
  }

  try {
    const body = await readFile(filePath)
    response.writeHead(200, {
      'content-type': mimeTypes[path.extname(filePath)] ?? 'application/octet-stream',
      'cache-control': 'no-store',
    })
    response.end(body)
  } catch {
    response.writeHead(404).end('not found')
  }
})

server.listen(port, '127.0.0.1', () => {
  console.log(`demo served from ${distDir} on http://127.0.0.1:${port}/`)
})
