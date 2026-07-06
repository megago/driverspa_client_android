# -*- coding: utf-8 -*-
from __future__ import unicode_literals, division
import os, math
from PIL import Image, ImageDraw, ImageFont, ImageFilter
from generate_assets import (mark_layer, round_rect, make_gradient, font,
                             C1, C2, WHITE, GFX)

SS = 2                      # supersample
W, H = 1024 * SS, 500 * SS


def sfont(bold, size):
    return font(bold, size * SS)


def over(base, layer, pos=(0, 0)):
    """Alpha-composite `layer` onto RGBA `base` at pos (Pillow 3.4 compatible)."""
    base.paste(layer, (int(pos[0]), int(pos[1])), layer)


def ring(d, box, color, w):
    """Stroked ellipse with thickness (Pillow 3.4 has no ellipse width)."""
    x0, y0, x1, y1 = box
    for i in range(max(1, int(w))):
        d.ellipse([x0 + i, y0 + i, x1 - i, y1 - i], outline=color)


def shear(img, k):
    """Italic-style horizontal shear of an RGBA layer."""
    w, h = img.size
    extra = int(abs(k) * h)
    canvas = Image.new("RGBA", (w + extra, h), (0, 0, 0, 0))
    canvas.paste(img, (extra if k > 0 else 0, 0), img)
    return canvas.transform((w + extra, h), Image.AFFINE, (1, -k, k * h if k > 0 else 0, 0, 1, 0),
                            resample=Image.BICUBIC)


def radial_glow(size, center, radius, color, strength=1.0):
    """Soft radial glow layer (RGBA)."""
    w, h = size
    layer = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    px = layer.load()
    cx, cy = center
    r2 = float(radius * radius)
    for y in range(h):
        for x in range(w):
            d2 = (x - cx) ** 2 + (y - cy) ** 2
            if d2 < r2:
                t = 1.0 - math.sqrt(d2 / r2)
                a = int(255 * (t ** 2) * strength)
                px[x, y] = (color[0], color[1], color[2], a)
    return layer


def phone(w, h, screen_builder):
    """Render a phone (RGBA) with a dark body and a custom screen."""
    pad = int(6 * SS)
    layer = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    r = int(46 * SS)
    # subtle outer rim highlight
    round_rect(d, [0, 0, w, h], r + 2, (90, 110, 140, 255))
    round_rect(d, [2, 2, w - 2, h - 2], r, (18, 26, 44, 255))
    # screen
    sr = int(30 * SS)
    sx0, sy0, sx1, sy1 = pad + 6 * SS, pad + 14 * SS, w - pad - 6 * SS, h - pad - 14 * SS
    screen = Image.new("RGBA", (int(sx1 - sx0), int(sy1 - sy0)), (0, 0, 0, 0))
    screen_builder(screen)
    # round the screen corners with a mask
    mask = Image.new("L", screen.size, 0)
    md = ImageDraw.Draw(mask)
    round_rect(md, [0, 0, screen.size[0], screen.size[1]], sr, 255)
    layer.paste(screen, (int(sx0), int(sy0)), mask)
    return layer


def app_screen(screen):
    """DriverSpa splash: brand-blue with concentric rings + mark + wordmark."""
    sw, sh = screen.size
    g = make_gradient(sw, sh, (0x1B, 0x4B, 0xE0), (0x0B, 0x2A, 0x7A), downscale=1).convert("RGBA")
    screen.paste(g, (0, 0))
    d = ImageDraw.Draw(screen, "RGBA")
    cx, cy = sw / 2.0, sh * 0.42
    for i in range(5, 0, -1):
        rr = sw * 0.10 * i
        ring(d, [cx - rr, cy - rr, cx + rr, cy + rr], (255, 255, 255, 30), 2 * SS)
    # mark
    scale = (sw * 0.32) / 14.0
    ml = mark_layer(max(sw, sh), scale, cx, cy, WHITE, 1.6)
    over(screen, ml.crop((0, 0, sw, sh)))
    # wordmark
    f = sfont(True, 30)
    tw = d.textsize("DriverSpa", font=f)[0]
    d.text(((sw - tw) / 2, sh * 0.62), "DriverSpa", font=f, fill=WHITE)


def back_screen(screen):
    sw, sh = screen.size
    g = make_gradient(sw, sh, (0x10, 0x2A, 0x55), (0x08, 0x14, 0x30), downscale=1).convert("RGBA")
    screen.paste(g, (0, 0))
    d = ImageDraw.Draw(screen, "RGBA")
    cx, cy = sw / 2.0, sh / 2.0
    for i in range(5, 0, -1):
        rr = sw * 0.11 * i
        ring(d, [cx - rr, cy - rr, cx + rr, cy + rr], (80, 140, 255, 40), 2 * SS)


def draw_car(base, cx, cy, length):
    """Stylized blue sports-coupe silhouette (facing left) with headlight glow."""
    s = length / 200.0
    L = Image.new("RGBA", (int(230 * s), int(150 * s)), (0, 0, 0, 0))
    d = ImageDraw.Draw(L, "RGBA")

    def P(x, y):
        return (x * s, (y + 20) * s)

    body = [P(4, 60), P(0, 50), P(10, 40), P(30, 37), P(58, 33),
            P(80, 14), P(126, 11), P(152, 20), P(176, 33), P(200, 40),
            P(202, 56), P(198, 68), P(150, 70), P(70, 70), P(22, 70), P(6, 66)]
    # body gradient via two-tone fill + highlight
    d.polygon(body, fill=(0x1E, 0x74, 0xFF, 255))
    # window glass
    glass = [P(62, 33), P(82, 17), P(122, 15), P(146, 22), P(120, 34)]
    d.polygon(glass, fill=(0xBF, 0xE6, 0xFF, 255))
    # lower shade
    d.polygon([P(6, 66), P(22, 70), P(70, 70), P(150, 70), P(198, 68), P(200, 62),
               P(150, 64), P(70, 64), P(20, 64)], fill=(0x12, 0x4A, 0xB8, 255))
    # side highlight streak
    d.line([P(20, 48), P(180, 44)], fill=(0x7F, 0xC4, 0xFF, 220), width=int(3 * s))
    # wheels
    for wx in (52, 156):
        r = 24 * s
        d.ellipse([wx * s - r, (58 + 20) * s - r, wx * s + r, (58 + 20) * s + r], fill=(12, 16, 26, 255))
        r2 = 12 * s
        ring(d, [wx * s - r2, (58 + 20) * s - r2, wx * s + r2, (58 + 20) * s + r2],
             (120, 180, 255, 255), 3 * s)
    # headlight
    hx, hy = P(6, 46)
    d.ellipse([hx - 6 * s, hy - 5 * s, hx + 6 * s, hy + 5 * s], fill=(230, 245, 255, 255))
    ox, oy = int(cx - L.size[0] / 2), int(cy - L.size[1] / 2)
    over(base, draw_car_glow(L.size, (hx, hy), 26 * s), (ox, oy))
    over(base, L, (ox, oy))


def draw_car_glow(size, center, radius):
    return radial_glow(size, center, radius, (150, 210, 255), 0.9)


def build():
    # ---- background ----
    img = make_gradient(W, H, (0x0B, 0x12, 0x24), (0x07, 0x0C, 0x18), downscale=2).convert("RGBA")
    # right-side brand glow (behind phones/car)
    over(img, radial_glow((W, H), (int(W * 0.66), int(H * 0.5)), int(H * 0.95),
                          (0x18, 0x5A, 0xE0), 0.85))
    # left concentric rings behind text
    d = ImageDraw.Draw(img, "RGBA")
    lcx, lcy = int(W * 0.20), int(H * 0.50)
    over(img, radial_glow((W, H), (lcx, lcy), int(H * 0.55), (0x12, 0x3A, 0x9C), 0.7))
    for i in range(6, 0, -1):
        rr = H * 0.075 * i
        ring(d, [lcx - rr, lcy - rr, lcx + rr, lcy + rr], (90, 150, 255, 45), 2 * SS)

    # ---- phones ----
    pw, ph = int(190 * SS), int(390 * SS)
    back = phone(pw, ph, back_screen).rotate(20, expand=True, resample=Image.BICUBIC)
    front = phone(pw, ph, app_screen).rotate(-12, expand=True, resample=Image.BICUBIC)
    over(img, back, (int(W * 0.58), int(H * 0.16)))

    # ---- car (between phones) ----
    draw_car(img, int(W * 0.55), int(H * 0.46), 300 * SS)

    over(img, front, (int(W * 0.70), int(H * 0.20)))

    # ---- headline text ----
    tl = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    td = ImageDraw.Draw(tl)
    f = sfont(True, 60)
    td.text((0, int(H * 0.30)), "СКАЧАЙ", font=f, fill=WHITE)
    td.text((0, int(H * 0.48)), "СЕЙЧАС!", font=f, fill=WHITE)
    tl = shear(tl, 0.18)
    # soft glow shadow
    glow = tl.filter(ImageFilter.GaussianBlur(6 * SS))
    over(img, glow, (int(W * 0.045), int(H * 0.02)))
    over(img, tl, (int(W * 0.05), 0))

    out = img.convert("RGB").resize((1024, 500), Image.LANCZOS)
    p = os.path.join(GFX, "feature_graphic_promo_1024x500.png")
    out.save(p)
    print("saved ->", p)


if __name__ == "__main__":
    build()
