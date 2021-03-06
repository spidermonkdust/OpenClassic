package ch.spacebase.openclassic.client.gui;

import java.util.ArrayList;
import java.util.List;

import ch.spacebase.openclassic.api.OpenClassic;
import ch.spacebase.openclassic.api.gui.GuiScreen;
import ch.spacebase.openclassic.api.gui.widget.Button;
import ch.spacebase.openclassic.api.gui.widget.ButtonList;
import ch.spacebase.openclassic.api.render.RenderHelper;
import ch.spacebase.openclassic.api.translate.Language;
import ch.spacebase.openclassic.client.util.GeneralUtils;

public class LanguageScreen extends GuiScreen {

	private GuiScreen parent;

	public LanguageScreen(GuiScreen parent) {
		this.parent = parent;
	}

	public void onOpen() {
		this.clearWidgets();
		this.attachWidget(new ButtonList(0, this.getWidth(), this.getHeight(), this));
		this.attachWidget(new Button(1, this.getWidth() / 2 - 75, this.getHeight() / 6 + 156, 150, 20, this, OpenClassic.getGame().getTranslator().translate("gui.back")));

		List<String> languages = new ArrayList<String>();
		for (Language language : OpenClassic.getGame().getTranslator().getLanguages()) {
			languages.add(language.getName() + " (" + language.getLangCode() + ")");
		}

		this.getWidget(0, ButtonList.class).setContents(languages);
	}

	public final void onButtonClick(Button button) {
		if(button.isActive()) {
			if(button.getId() == 1) {
				GeneralUtils.getMinecraft().setCurrentScreen(this.parent);
			}
		}
	}

	@Override
	public void onButtonListClick(ButtonList list, Button button) {
		if(button.isActive()) {
			String code = button.getText().substring(button.getText().indexOf('(') + 1, button.getText().indexOf(')'));
			OpenClassic.getGame().getConfig().setValue("options.language", code);
			GeneralUtils.getMinecraft().setCurrentScreen(new LanguageScreen(this.parent));
			OpenClassic.getGame().getConfig().save();
		}
	}

	public void render() {
		RenderHelper.getHelper().drawDefaultBG();
		RenderHelper.getHelper().renderText(OpenClassic.getGame().getTranslator().translate("gui.language.select"), this.getWidth() / 2, 15, 16777215);
		RenderHelper.getHelper().renderText(String.format(OpenClassic.getGame().getTranslator().translate("gui.language.current"), OpenClassic.getGame().getConfig().getString("options.language")), this.getWidth() / 2, this.getHeight() / 2 + 48, 16777215);
		super.render();
	}
}
