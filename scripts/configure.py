import os, re, subprocess, urllib.request
from xml.sax.saxutils import escape
e = os.environ
color = e.get('SPLASH_COLOR') or '#000000'
if not re.fullmatch(r'#[0-9a-fA-F]{6}', color): color = '#000000'
def b(v): return 'true' if (v or '').lower() == 'true' else 'false'
os.makedirs('app/src/main/res/values', exist_ok=True)
with open('app/src/main/res/values/config.xml', 'w', encoding='utf-8') as f:
    f.write(f'''<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="app_name">{escape(e['APP_NAME'][:50]).replace("'", "\\'")}</string>
  <string name="cfg_url">{escape(e['APP_URL'])}</string>
  <string name="cfg_orientation">{escape(e.get('ORIENTATION','Auto'))}</string>
  <string name="cfg_display">{escape(e.get('DISPLAY_MODE','Fullscreen'))}</string>
  <string name="cfg_splash">{color}</string>
  <color name="splash">{color}</color>
  <bool name="cfg_keep_on">{b(e.get('KEEP_ON'))}</bool>
  <bool name="cfg_pull_refresh">{b(e.get('PULL_REFRESH'))}</bool>
</resources>
''')
d = 'app/src/main/res/mipmap-xxxhdpi'
os.makedirs(d, exist_ok=True)
out = d + '/ic_launcher.png'
ok = False
if e.get('ICON_URL'):
    try:
        req = urllib.request.Request(e['ICON_URL'], headers={'User-Agent': 'Mozilla/5.0'})
        open('/tmp/icon_src', 'wb').write(urllib.request.urlopen(req, timeout=30).read())
        subprocess.run(['convert', '/tmp/icon_src[0]', '-resize', '192x192^', '-gravity', 'center', '-extent', '192x192', out], check=True)
        ok = True
    except Exception as ex:
        print('icon failed', ex)
if not ok:
    subprocess.run(['convert', '-size', '192x192', 'xc:' + color, out], check=True)
rid = re.sub(r'[^a-z0-9]', '', e['REQUEST_ID'].lower())
with open(os.environ['GITHUB_ENV'], 'a') as f:
    f.write(f"APP_ID=app.apkmaker.a{rid}\n")
    v = e.get('VERSION_NAME') or '1.0.0'
    f.write(f"VER_NAME={re.sub(r'[^0-9A-Za-z._-]', '', v) or '1.0.0'}\n")
