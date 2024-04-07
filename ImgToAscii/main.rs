use image::GenericImageView;
use std::env;

fn main() {
    let args: Vec<_> = env::args().collect();
    //let greyscale = String::from("@B$%8&#WMwhkbdpqmaoZX0QOLCJUzcvnxrjftu/()1{}[]?+~<>i!lI;:.");
    let greyscale = String::from("@%#BI*+=~-:.");
    let img = image::open(&args[1]).unwrap();
    for (x, _, pixel) in img.pixels()
    {
        if x == 0
        {
            println!("");
        }
        //let mut avg: usize = (pixel.0[0] as usize + pixel.0[1] as usize + pixel.0[2] as usize) / 3;
        let avg = ((pixel.0[0] as f64 * 0.299)) + ((pixel.0[1] as f64 * 0.587)) + ((pixel.0[0] as f64 * 0.114));
        let mut avg = (avg / 255.0 * greyscale.len() as f64) as usize;
        if avg > 0
        {
            avg = avg - 1;
        }
        print!("{} ", greyscale.chars().nth(avg).unwrap());
    }
}
