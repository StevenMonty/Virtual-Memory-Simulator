public class tmp{

	public static void main(String args[]){


		for(int block = 0; block < 20; block++){


			int _byte = (block / 8);
			int bit = (block%8);

			System.out.printf("block: %d  byte: %d bit:%d\n",block,  _byte, bit);
		}
		
		System.out.println("\n\n");
		for(int byte_num = 0; byte_num < 3; byte_num++){
			for(int bit_num = 0; bit_num < 8; bit_num++) {
				int block = (byte_num * 8) + bit_num;
				
				System.out.println(block);
			}
		}

	}

}
