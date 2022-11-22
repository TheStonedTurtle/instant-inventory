/*
 * Copyright (c) 2022 Elg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.elg.ii.clean;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import no.elg.ii.Feature;
import no.elg.ii.InstantInventoryConfig;
import no.elg.ii.InventoryState;

@Singleton
public class CleanHerbFeature implements Feature {

  public static final String CLEAN_OPTION = "Clean";

  @Inject
  private CleanHerbOverlay overlay;
  @Inject
  private OverlayManager overlayManager;
  @Inject
  private InstantInventoryConfig config;
  @Inject
  private Client client;

  private final InventoryState state = new InventoryState();

  @Override
  public void onEnable() {
    overlayManager.add(overlay);
  }

  @Override
  public void onDisable() {
    overlayManager.remove(overlay);
    overlay.invalidateCache();
  }

  @Subscribe
  public void onMenuOptionClicked(final MenuOptionClicked event) {
    Widget widget = event.getWidget();
    if (widget != null) {
      String menuOption = event.getMenuOption();
      if (config.instantDrop() && CLEAN_OPTION.equals(menuOption)) {
        int itemId = event.getItemId();
        HerbInfo herbInfo = HerbInfo.HERBS.get(itemId);
        if (herbInfo == null) {
          return;
        }

        //TODO test with spicy stew (brown) by reducing the boosted level
        int herbloreLevel = client.getBoostedSkillLevel(Skill.HERBLORE);
        if (herbloreLevel >= herbInfo.getMinLevel()) {
          state.setItemId(widget.getIndex(), itemId);
        }
      }
    }
  }

  @Override
  @Nonnull
  public InventoryState getState() {
    return state;
  }
}
