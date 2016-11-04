if not CURSES then ui.set_theme('base16-default-dark') end

local function fontsize()
  local c = _SCINTILLA.constants
  local buffer = buffer
  buffer.zoom = -1 -- e.g. add 2 points to the font size
  buffer.margin_width_n[0] = 4 + 3 * buffer:text_width(c.STYLE_LINENUMBER, '9')
end

events.connect(events.BUFFER_AFTER_SWITCH, fontsize)
events.connect(events.VIEW_AFTER_SWITCH, fontsize)
