---
name: Kijani Market
colors:
  surface: '#fbf9f4'
  surface-dim: '#dbdad5'
  surface-bright: '#fbf9f4'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3ee'
  surface-container: '#f0eee9'
  surface-container-high: '#eae8e3'
  surface-container-highest: '#e4e2dd'
  on-surface: '#1b1c19'
  on-surface-variant: '#42493e'
  inverse-surface: '#30312e'
  inverse-on-surface: '#f2f1ec'
  outline: '#72796e'
  outline-variant: '#c2c9bb'
  surface-tint: '#3b6934'
  primary: '#154212'
  on-primary: '#ffffff'
  primary-container: '#2d5a27'
  on-primary-container: '#9dd090'
  inverse-primary: '#a1d494'
  secondary: '#4a654f'
  on-secondary: '#ffffff'
  secondary-container: '#c9e7cc'
  on-secondary-container: '#4e6953'
  tertiary: '#6e1a0f'
  on-tertiary: '#ffffff'
  tertiary-container: '#8d3123'
  on-tertiary-container: '#ffaea0'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#bcf0ae'
  primary-fixed-dim: '#a1d494'
  on-primary-fixed: '#002201'
  on-primary-fixed-variant: '#23501e'
  secondary-fixed: '#cceacf'
  secondary-fixed-dim: '#b0ceb4'
  on-secondary-fixed: '#062010'
  on-secondary-fixed-variant: '#334d38'
  tertiary-fixed: '#ffdad4'
  tertiary-fixed-dim: '#ffb4a7'
  on-tertiary-fixed: '#400200'
  on-tertiary-fixed-variant: '#80281b'
  background: '#fbf9f4'
  on-background: '#1b1c19'
  surface-variant: '#e4e2dd'
typography:
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.05em
  price-display:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 22px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  margin-mobile: 16px
  margin-desktop: 32px
  gutter: 12px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style

The design system is rooted in the "Modern Organic" movement, blending the editorial richness of a lifestyle magazine with the functional clarity of a premium marketplace. It is designed to evoke the feeling of a sun-drenched farmers' market—fresh, tactile, and communal.

**Brand Personality:**
- **Fresh:** High-quality imagery and plenty of white space (Warm Cream) to let products breathe.
- **Organic:** Soft, oversized radii and natural color transitions.
- **Trustworthy:** Grounded by deep forest greens and structured, legible typography.
- **Community-focused:** Design patterns that emphasize the story behind the produce and the farmer.

**Visual Style:**
The aesthetic utilizes a **Staggered Minimalism** approach. It borrows the masonry layout popularized by Pinterest to create a sense of discovery and variety. This is paired with "Soft-Touch" UI—avoiding harsh lines in favor of deep rounded corners and subtle ambient shadows to create a friendly, approachable interface.

## Colors

The palette is derived from nature to reinforce the farm-to-table narrative.

- **Primary (Deep Forest Green):** Used for core branding, primary calls to action, and high-level navigation. It provides the "anchor" for the brand.
- **Secondary (Sage Green):** Applied to success states, active filters, and subtle backgrounds to create a soft, monochromatic layering effect.
- **Tertiary (Terracotta):** Reserved for highlights, price points, badges (e.g., "Organic", "New"), and essential notifications. Its warmth contrasts the greens to draw the eye.
- **Neutral (Warm Cream):** This is the base "paper" of the app. It replaces pure white to reduce eye strain and provide a more artisanal, premium feel.
- **Text:** Deep Forest Green is used at high opacity for headings; a desaturated version is used for body copy to maintain softness.

## Typography

This design system utilizes **Plus Jakarta Sans** for its friendly yet modern geometric construction. Its high x-height and open apertures ensure maximum legibility for product descriptions and pricing on mobile screens.

**Usage Guidelines:**
- **Headlines:** Use tight letter-spacing for large titles to give them a modern, editorial look.
- **Price Display:** Always set in bold weights using the Terracotta color to ensure clear information hierarchy.
- **Label-Caps:** Use for small metadata like "Weight/kg" or "Distance" to differentiate from body text without increasing size.

## Layout & Spacing

The design system employs a **Staggered Fluid Grid** to mirror the Pinterest aesthetic.

- **Grid Model:** A 2-column masonry layout for mobile devices, expanding to 4 or 6 columns for larger screens. 
- **Staggering:** Cards within the grid should have varying aspect ratios (3:4, 1:1, 4:5) to create a dynamic, curated visual rhythm.
- **Margins:** A consistent 16px safe area on mobile ensures content doesn't feel cramped.
- **Gutters:** Tight 12px gutters between cards prioritize imagery over whitespace in the shop feed.

## Elevation & Depth

To maintain the soft, organic feel, the design system avoids heavy shadows or high-contrast borders.

- **The Surface:** The primary background is always Warm Cream.
- **Ambient Depth:** Cards use a "Deep-Soft" shadow: a Y-offset of 4px, a 20px blur, and a very low opacity (5-8%) of the Deep Forest Green color. This makes elements feel like they are resting gently on the cream surface rather than floating high above it.
- **Active State:** When pressed, cards should scale slightly (0.98x) and the shadow should tighten, simulating physical pressure.

## Shapes

The shape language is the defining characteristic of this design system. It utilizes exaggerated, friendly curves to communicate approachability.

- **Base Radius:** All primary product cards and containers use a **24px (rounded-xl)** radius.
- **Input Fields:** Search bars and text inputs use a pill-shaped (fully rounded) radius to encourage interaction.
- **Buttons:** Primary buttons are pill-shaped to stand out against the rectangular but rounded product cards.
- **Chips:** Small tags for "Organic" or "Local" utilize a 12px radius to remain distinct from the larger card shapes.

## Components

### Buttons & Inputs
- **Primary Button:** Pill-shaped, Deep Forest Green background with Warm Cream text. Use for "Add to Basket" or "Checkout."
- **Search Bar:** High prominence. Pill-shaped, Warm Cream background (slightly darker than the page) with a thin Sage Green stroke. Positioned at the top of the feed with a subtle shadow.

### Cards
- **Product Card:** 24px radius. The image is the hero, occupying the top 70% of the card. Content (Title, Price) is left-aligned on the Warm Cream base.
- **Staggered Feed:** Elements should appear to "flow" down the screen.

### Navigation & Tags
- **Category Chips:** Horizontal scrolling list of Sage Green chips with Deep Forest Green text. Used for quick filtering (e.g., "Vegetables", "Dairy").
- **Badges:** Small circular icons or labels in Terracotta used for discounts or "Direct from Farm" status.

### Lists
- **Order History:** Uses a flat-card style with Sage Green dividers rather than lines, maintaining the soft look.