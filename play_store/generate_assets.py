# -*- coding: utf-8 -*-
from __future__ import unicode_literals, division
import os, math
from PIL import Image, ImageDraw, ImageFont

OUT = os.path.dirname(os.path.abspath(__file__))
GFX = os.path.join(OUT, "graphics")
if not os.path.isdir(GFX):
    os.makedirs(GFX)

# ---- Brand ----
C1 = (0x25, 0x63, 0xFF)   # #2563FF
C2 = (0x00, 0xD4, 0xC8)   # #00D4C8
WHITE = (255, 255, 255)

FONT = "C:/Windows/Fonts/segoeui.ttf"
FONT_B = "C:/Windows/Fonts/segoeuib.ttf"
FONT_SB = "C:/Windows/Fonts/seguisb.ttf"


def font(bold, size):
    return ImageFont.truetype(FONT_B if bold else FONT, size)


def lerp(a, b, t):
    return int(round(a + (b - a) * t))


def make_gradient(w, h, c1, c2, downscale=2):
    """Diagonal (TL->BR) linear gradient. Rendered small then upscaled."""
    sw, sh = max(1, w // downscale), max(1, h // downscale)
    base = Image.new("RGB", (sw, sh))
    px = base.load()
    maxd = float((sw - 1) + (sh - 1)) or 1.0
    # precompute per-diagonal color
    colors = []
    for s in range(sw + sh):
        t = s / maxd
        if t > 1:
            t = 1.0
        colors.append((lerp(c1[0], c2[0], t), lerp(c1[1], c2[1], t), lerp(c1[2], c2[2], t)))
    for y in range(sh):
        row = colors
        for x in range(sw):
            px[x, y] = row[x + y]
    if (sw, sh) != (w, h):
        base = base.resize((w, h), Image.BICUBIC)
    return base


def svg_arc(p0, p1, r, large_arc, sweep, n=28):
    x1, y1 = p0
    x2, y2 = p1
    rx = ry = float(r)
    dx = (x1 - x2) / 2.0
    dy = (y1 - y2) / 2.0
    x1p, y1p = dx, dy
    lam = (x1p * x1p) / (rx * rx) + (y1p * y1p) / (ry * ry)
    if lam > 1:
        s = math.sqrt(lam)
        rx *= s
        ry *= s
    rx2, ry2 = rx * rx, ry * ry
    x1p2, y1p2 = x1p * x1p, y1p * y1p
    sign = -1 if large_arc == sweep else 1
    num = rx2 * ry2 - rx2 * y1p2 - ry2 * x1p2
    den = rx2 * y1p2 + ry2 * x1p2
    co = sign * math.sqrt(max(0.0, num / den)) if den else 0.0
    cxp = co * (rx * y1p / ry)
    cyp = co * (-ry * x1p / rx)
    cx = cxp + (x1 + x2) / 2.0
    cy = cyp + (y1 + y2) / 2.0

    def ang(ux, uy, vx, vy):
        dot = ux * vx + uy * vy
        lu = math.sqrt(ux * ux + uy * uy)
        lv = math.sqrt(vx * vx + vy * vy)
        a = math.acos(max(-1.0, min(1.0, dot / (lu * lv))))
        if ux * vy - uy * vx < 0:
            a = -a
        return a

    ux, uy = (x1p - cxp) / rx, (y1p - cyp) / ry
    vx, vy = (-x1p - cxp) / rx, (-y1p - cyp) / ry
    theta1 = ang(1, 0, ux, uy)
    dtheta = ang(ux, uy, vx, vy)
    if not sweep and dtheta > 0:
        dtheta -= 2 * math.pi
    if sweep and dtheta < 0:
        dtheta += 2 * math.pi
    pts = []
    for i in range(n + 1):
        t = theta1 + dtheta * i / float(n)
        pts.append((cx + rx * math.cos(t), cy + ry * math.sin(t)))
    return pts


def polyline(draw, pts, width, color):
    r = width / 2.0
    ipts = [(p[0], p[1]) for p in pts]
    draw.line(ipts, fill=color, width=int(round(width)))
    for p in ipts:
        draw.ellipse([p[0] - r, p[1] - r, p[0] + r, p[1] + r], fill=color)


def draw_mark(draw, tf, unit_w, color):
    """Car-wash mark (matches app adaptive-icon foreground)."""
    lw = unit_w
    # water spray
    for (a, b) in [((8, 2), (6.7, 4.4)), ((12, 1.6), (10.7, 4.2)), ((16, 2), (14.7, 4.4))]:
        polyline(draw, [tf(*a), tf(*b)], lw, color)
    # car body outline
    body = [(5, 14), (6.5, 9.5)]
    body += svg_arc((6.5, 9.5), (8.4, 8), 2, 0, 1)
    body += [(15.6, 8)]
    body += svg_arc((15.6, 8), (17.5, 9.5), 2, 0, 1)
    body += [(19, 14)]
    polyline(draw, [tf(*p) for p in body], lw, color)
    # chassis + struts
    polyline(draw, [tf(5, 14), tf(19, 14)], lw, color)
    polyline(draw, [tf(5, 14), tf(5, 18)], lw, color)
    polyline(draw, [tf(19, 14), tf(19, 18)], lw, color)
    # wheels
    r = lw / 2.0
    for (wx, wy) in [(7, 18), (17, 18)]:
        cx, cy = tf(wx, wy)
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=color)


def round_rect(draw, box, radius, fill):
    x0, y0, x1, y1 = box
    r = radius
    draw.rectangle([x0 + r, y0, x1 - r, y1], fill=fill)
    draw.rectangle([x0, y0 + r, x1, y1 - r], fill=fill)
    draw.pieslice([x0, y0, x0 + 2 * r, y0 + 2 * r], 180, 270, fill=fill)
    draw.pieslice([x1 - 2 * r, y0, x1, y0 + 2 * r], 270, 360, fill=fill)
    draw.pieslice([x0, y1 - 2 * r, x0 + 2 * r, y1], 90, 180, fill=fill)
    draw.pieslice([x1 - 2 * r, y1 - 2 * r, x1, y1], 0, 90, fill=fill)


def mark_layer(size, scale, cx, cy, color, unit_w):
    """Render mark centered at (cx,cy) on a transparent layer of given size."""
    layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    # mark bbox center in unit space ~ (12, 9.8)
    ox = cx - 12 * scale
    oy = cy - 9.8 * scale

    def tf(x, y):
        return (ox + x * scale, oy + y * scale)

    draw_mark(d, tf, unit_w * scale, color)
    return layer


# =====================================================================
# 1) PLAY STORE ICON  512x512  (32-bit PNG)
# =====================================================================
def build_icon():
    SS = 4
    S = 512 * SS
    base = make_gradient(S, S, C1, C2, downscale=4).convert("RGBA")
    scale = 67 * SS / 4.0 * 4  # tuned below
    # place mark: width 14 units -> ~0.46*512
    scale = (0.46 * 512 * SS) / 14.0
    layer = mark_layer(S, scale, S / 2.0, S / 2.0, WHITE, 1.6)
    base = Image.alpha_composite(base, layer)
    base = base.resize((512, 512), Image.LANCZOS)
    base.save(os.path.join(GFX, "play_store_icon_512.png"))
    print("icon 512x512 ->", "play_store_icon_512.png")


# =====================================================================
# 2) FEATURE GRAPHIC  1024x500  (24-bit PNG, no alpha)
# =====================================================================
def build_feature():
    W, H = 1024, 500
    img = make_gradient(W, H, C1, C2, downscale=1).convert("RGBA")
    # soft white badge with mark on the left
    badge = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bd = ImageDraw.Draw(badge)
    round_rect(bd, [70, 120, 330, 380], 60, (255, 255, 255, 38))
    img = Image.alpha_composite(img, badge)
    scale = (150.0) / 14.0
    layer = mark_layer(max(W, H), scale, 200, 250, WHITE, 1.6)
    img = Image.alpha_composite(img, layer.crop((0, 0, W, H)))
    d = ImageDraw.Draw(img)
    d.text((390, 150), "DriverSpa", font=font(True, 96), fill=WHITE)
    d.text((394, 268), "Мойка авто по записи —", font=font(False, 42), fill=(235, 245, 255, 255))
    d.text((394, 322), "быстро, рядом и без очередей", font=font(False, 42), fill=(235, 245, 255, 255))
    img = img.convert("RGB")
    img.save(os.path.join(GFX, "feature_graphic_1024x500.png"))
    print("feature 1024x500 ->", "feature_graphic_1024x500.png")


# =====================================================================
# 3) SCREENSHOT TEMPLATES  1080x1920
# =====================================================================
def screenshot(fname, headline, sub, builder):
    W, H = 1080, 1920
    img = Image.new("RGB", (W, H), (245, 248, 252))
    # top gradient banner
    band = make_gradient(W, 620, C1, C2, downscale=1)
    img.paste(band, (0, 0))
    d = ImageDraw.Draw(img)
    # small brand row
    layer = mark_layer(150, 150 / 14.0, 75, 75, WHITE, 1.6)
    img.paste(layer, (46, 60), layer)
    d.text((225, 78), "DriverSpa", font=font(True, 52), fill=WHITE)
    # headline
    d.text((60, 250), headline, font=font(True, 76), fill=WHITE)
    d.text((60, 360), sub, font=font(False, 40), fill=(235, 245, 255))
    # content card
    builder(img, d)
    img.save(os.path.join(GFX, fname))
    print("screenshot ->", fname)


def stars(d, x, y, size, n=5, filled=5, color=(255, 193, 7)):
    for i in range(n):
        cx = x + i * (size * 1.25)
        pts = []
        for k in range(10):
            ang = -math.pi / 2 + k * math.pi / 5
            rr = size / 2.0 if k % 2 == 0 else size / 4.6
            pts.append((cx + rr * math.cos(ang), y + rr * math.sin(ang)))
        d.polygon(pts, fill=color if i < filled else (210, 216, 224))


def sc1(img, d):
    round_rect(d, [60, 560, 1020, 1240], 40, (255, 255, 255))
    round_rect(d, [60, 560, 1020, 900], 40, (225, 236, 255))
    # map pin
    px, py = 540, 720
    d.ellipse([px - 60, py - 60, px + 60, py + 60], fill=C1)
    d.polygon([(px - 42, py + 30), (px + 42, py + 30), (px, py + 110)], fill=C1)
    d.ellipse([px - 24, py - 24, px + 24, py + 24], fill=(255, 255, 255))
    for (lx, ly) in [(240, 640), (820, 680), (760, 840)]:
        d.ellipse([lx - 14, ly - 14, lx + 14, ly + 14], fill=(0, 0xD4, 0xC8))
    d.text((110, 980), "АвтоСпа на Абая", font=font(True, 46), fill=(30, 40, 60))
    d.text((110, 1050), "0.8 км · открыто до 22:00", font=font(False, 36), fill=(120, 130, 145))
    stars(d, 118, 1160, 44, filled=5)
    d.text((360, 1138), "4.9 · 214 отзывов", font=font(False, 36), fill=(120, 130, 145))


def sc2(img, d):
    round_rect(d, [60, 560, 1020, 1320], 40, (255, 255, 255))
    d.text((110, 610), "Выберите время", font=font(True, 48), fill=(30, 40, 60))
    slots = ["09:00", "10:30", "12:00", "13:30", "15:00", "16:30"]
    for i, s in enumerate(slots):
        col = i % 3
        rowi = i // 3
        x0 = 110 + col * 300
        y0 = 730 + rowi * 150
        active = (i == 1)
        round_rect(d, [x0, y0, x0 + 250, y0 + 110], 24,
                   C1 if active else (235, 240, 248))
        tw = d.textsize(s, font=font(True, 44))[0]
        d.text((x0 + (250 - tw) / 2, y0 + 30), s, font=font(True, 44),
               fill=(255, 255, 255) if active else (60, 70, 90))
    round_rect(d, [110, 1140, 970, 1250], 30, (0, 0xD4, 0xC8))
    tw = d.textsize("Забронировать", font=font(True, 46))[0]
    d.text((540 - tw / 2, 1168), "Забронировать", font=font(True, 46), fill=WHITE)


def sc3(img, d):
    round_rect(d, [60, 560, 1020, 1300], 40, (255, 255, 255))
    d.text((110, 610), "Отзывы клиентов", font=font(True, 48), fill=(30, 40, 60))
    revs = [("Айгерим", 5, "Машину помыли быстро, запись работает отлично!"),
            ("Данияр", 5, "Удобно бронировать время, не жду в очереди."),
            ("Марат", 4, "Хорошие цены и вежливый персонал.")]
    y = 730
    for name, st, txt in revs:
        d.ellipse([110, y, 180, y + 70], fill=(225, 236, 255))
        d.text((128, y + 12), name[0], font=font(True, 40), fill=C1)
        d.text((210, y + 4), name, font=font(True, 40), fill=(30, 40, 60))
        stars(d, 214, y + 74, 30, filled=st)
        d.text((110, y + 110), txt, font=font(False, 34), fill=(90, 100, 115))
        y += 210


def build_screenshots():
    screenshot("screenshot_1_map.png", "Автомойки рядом", "Найдите ближайшую мойку на карте", sc1)
    screenshot("screenshot_2_booking.png", "Запись онлайн", "Забронируйте удобное время за минуту", sc2)
    screenshot("screenshot_3_reviews.png", "Отзывы и рейтинг", "Выбирайте лучшие автомойки", sc3)


if __name__ == "__main__":
    build_icon()
    build_feature()
    build_screenshots()
    print("DONE ->", GFX)
