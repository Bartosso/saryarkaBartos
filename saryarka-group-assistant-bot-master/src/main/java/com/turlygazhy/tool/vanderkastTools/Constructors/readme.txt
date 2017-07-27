Package Constructors:

This package uses for esealy constructing some of routine entities in TelegramBotApi.

1. InlineKeyboardConstructor - constructing inline keyboards
	Methods:
		+ public InlineKeyboardMarkup getKeyboard(ArrayList<String> buttons, ArrayList<String> callbackData, KeyboardPattern pattern)
			returns inline keyboard with: button text = buttons;
						(optional) call back data = callBackData;
						(optional) buttons in row = pattern (check down "KeyboardPattern);
		watch another similar methods
		
		+ public List<InlineKeyboardButton> setButtonsInRow(ArrayList<String> buttons, ArrayList<String> callBackData, KeyboardPattern pattern)
			returns buttons row (maybe for adding in keyboard)

			
2. ReplyKeyboardConstructor - constructing reply keyboards
	Methods:
		+ public KeyboardRow setButtonsInRow(ArrayList<String> buttons, KeyboardPattern pattern)
			returns buttons row 
		
		+ public ReplyKeyboardMarkup AddContactRequestInKeyboard(ReplyKeyboardMarkup keyboard, int row, int posInRow)
			returns keyboard with added contact request in button with position (row ; posInRow)
		
		+ public ReplyKeyboardMarkup AddLocationRequestInKeyboard(ReplyKeyboardMarkup keyboard, int row, int posInRow)
			returns keyboard with added location request in button with position (row ; posInRow)
				+ public static ReplyKeyboardMarkup addContactRequestInKeyboard(ReplyKeyboardMarkup keyboard, String buttonText)
			returns keyboard with added contact request in button witch found by buttonText
					
		+ public static ReplyKeyboardMarkup addLocationRequestInKeyboard(ReplyKeyboardMarkup keyboard, String buttonText)
			returns keyboard with added location request in button witch found by buttonText

3. MessageConstructor - constructing SendMessages
	Methods:
		+ public SendMessage getSendMessage(String text, long chat_id, String parseMode, Keyboard keyboard);
			returns SendMessage with : text = text;
						chatId = chat_id;
						(optional) ParseMode = parseMode ("Markdown" or "HTML");
						(optional) keyboard = keyboard (Inline- or Reply- KeyboardMarkup)
						
		
4. ButtonTool - helps keyboard constructors, but you can use it, if you found a way

	