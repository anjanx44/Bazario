import { Metadata } from 'next';
import Image from 'next/image';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import { Star, ChevronRight } from 'lucide-react';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import ImageGallery from '@/components/catalog/ImageGallery';
import ProductActions from '@/components/catalog/ProductActions';
import FrequentlyBoughtTogether from '@/components/catalog/FrequentlyBoughtTogether';
import { Product } from '@/types';

interface ProductPageProps {
  params: Promise<{
    slug: string;
  }>;
}

// Mock data fetching function
async function getProduct(slug: string): Promise<Product | null> {
  // In a real app, this would be a fetch to your API
  if (slug === 'aerosound-elite-5') {
    return {
      id: '1',
      name: 'AeroSound Elite 5 - Premium Noise-Cancelling Headphones',
      slug: 'aerosound-elite-5',
      description: 'The AeroSound Elite 5 redefines acoustic excellence. Equipped with our proprietary ANC 4.0 chip, these headphones analyze ambient noise 50,000 times per second to create an immersive bubble of silence, no matter where you are.',
      basePrice: 350.00,
      discountPrice: 299.00,
      discountPercentage: 15,
      sku: 'AS-ELITE5-BLK',
      rating: 4.5,
      reviewsCount: 120,
      active: true,
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM',
      images: [
        'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM',
        'https://lh3.googleusercontent.com/aida-public/AB6AXuCV54dkcMWwkMVBAqreY0UrmEIpcahUQn8gKhFq4YmBIi4IsuZvma6rp2juT2UBotGnR2bX7_u8zP2FDxaLUwc4WBSnNgDHoLm_rifzJ-hJLjTUDDHOoGQT6hlV4vtEkqXEIvy11YGAtekJum2QlBnV45yTqFMKfNFohLW92ldqFxoBgcHejEEDkbCH5_U3e6zwPCLdB1u1ixu0bGDWrRe_GTQrWRPuoNGazGKuTNpw1DVX1_oDQNeHu3pdUYPfnMMxq-PqaDxS7qU',
        'https://lh3.googleusercontent.com/aida-public/AB6AXuCewHoZeMQQEdBscS5Zz0-7GLBG8fQZMP2ZGmFLyvYUw1YgrzKcE6OiDGaEH6o9hhSCt_XXasINKhOTS8cPjMJ8-QNEHsd3c0vCpIju56rXwrbSI652k_rK7E39mckVRLonUZVWVDDV51BVWSYf62ka3qrpflQc7A-mFC60Y3TKJZlonHSbVnjI_mQy2vGSY3hN7PrTszbariSuKn28_yZjAWObv79ge_PfhdcOudpon6RKu1vFHzRzcaFQixJNSq9GJ_RCTE30t-E',
        'https://lh3.googleusercontent.com/aida-public/AB6AXuDq6oW5d1F19ryK9868DOkAl1Lh7S8-pioZ-BODmGL9NPriFi-S4byu-lqEbODjMgayTVLmrNqUKIWuaTXkr9_wPdx1t-zcfyUlrBKU8gxMSX3mkx-STnOHzj2QFrvkK6OKjjnBUx_l-b0nN0OOn4MMPQZuG--oNdc0v1uLai42mBzCxY-JzsgSiDhxto6IS2IGm_PIV9YiCpfF76aSiNegGVkP2wdwTkWRxe4-sZu6Oupov85z-efG7sdOEN6qi_kzEsg_b-5RYqI'
      ],
      colors: [
        { name: 'Matte Black', hex: '#1A1A1A' },
        { name: 'Deep Sea', hex: '#1B2A4E' },
        { name: 'Silver Mist', hex: '#D1D5DB' }
      ],
      categoryName: 'Audio'
    };
  }
  return null;
}

export async function generateMetadata({ params }: ProductPageProps): Promise<Metadata> {
  const { slug } = await params;
  const product = await getProduct(slug);
  
  if (!product) return { title: 'Product Not Found' };
  
  return {
    title: `${product.name} | Bazario Store`,
    description: product.description,
  };
}

export default async function Page({ params }: ProductPageProps) {
  const { slug } = await params;
  const product = await getProduct(slug);

  if (!product) {
    notFound();
  }

  const suggestedItems = [
    {
      id: 'acc-1',
      name: 'Premium Aluminum Headphone Stand',
      price: 45.00,
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDd5jFOGlYISa1ZBE_vK1v5adB3sGxypq7NSag8-JP1ZTE52cWvYjOqubO5pvcRf2vW802h4DufYfImB6BKmQpyLYtXE3XW_YA_bFplx2DVw8MgxvaPOtHpX38B_iKo17YJl3U7Lk4s-ipE5EHQngUjunxGEoZMnzgPqKcEYnitpRqGDioJTYXNRhV7rN9czuWO32mfmPSED26N8BwsLkMaEhX0QWk8psUMBl5WpqbbwS3kSrOZBIa0kOWK8zuLj0YfyIw4sk-bPgM'
    },
    {
      id: 'acc-2',
      name: 'Hardshell Travel Protection Case',
      price: 29.00,
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDcF_uGQZAUTB1cYE7N6-IcBOGNUZyXg-S5yDifpnq60Pp8mmoqfAz523XN-mc_DFNBlPFFD_Dpsv5PI7MfGXprdnrWtAW7o1JiXUbQWvVlE60Tu5WArEcGZ3SW-mlx6xKxOvWXdn3RIRD_o3taFTOS_6ELs0S4YqCyCscD3XhoVKKPGS1qIhk54EZrlIkkbpwdwjI8mbE0dR1yCheybYd1nPrA7nA_D-32UwCehkJfSd9uuDQSFf2u4WvniskYgKdbD0saZrdg-LA'
    }
  ];

  return (
    <div className="min-h-screen bg-surface">
      <Header />
      
      <main className="max-w-container-max mx-auto px-margin-desktop py-stack-md">
        {/* Breadcrumbs */}
        <nav className="mb-stack-md">
          <ol className="flex items-center gap-2 text-label-md font-label-md text-on-surface-variant">
            <li><Link href="/" className="hover:text-primary">Home</Link></li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" /> 
              <Link href="/category/electronics" className="hover:text-primary">Electronics</Link>
            </li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" /> 
              <Link href="/category/audio" className="hover:text-primary">Audio</Link>
            </li>
            <li className="flex items-center gap-2 text-on-surface">
              <ChevronRight className="w-4 h-4" /> 
              {product.name.split(' - ')[0]}
            </li>
          </ol>
        </nav>

        {/* Product Core */}
        <section className="grid grid-cols-1 lg:grid-cols-2 gap-gutter items-start">
          <ImageGallery images={product.images || [product.imageUrl]} />
          
          <div className="flex flex-col gap-6 lg:pl-gutter">
            <div>
              <h1 className="font-headline-lg text-headline-lg text-on-surface mb-2">
                {product.name}
              </h1>
              <div className="flex items-center gap-4">
                <div className="flex items-center gap-1 text-tertiary">
                  {[...Array(4)].map((_, i) => (
                    <Star key={i} className="w-4 h-4 fill-current" />
                  ))}
                  <Star className="w-4 h-4" />
                  <span className="text-label-md font-label-md text-on-surface-variant ml-1">
                    ({product.reviewsCount} reviews)
                  </span>
                </div>
                <span className="px-2 py-0.5 bg-secondary-container text-on-secondary-container text-label-md font-label-md rounded-full">
                  In Stock
                </span>
              </div>
            </div>

            <div className="flex items-center gap-4 py-4 border-y border-outline-variant">
              <span className="text-price-lg font-price-lg text-on-surface">
                ${product.discountPrice?.toFixed(2) || product.basePrice.toFixed(2)}
              </span>
              {product.discountPrice && (
                <>
                  <span className="text-body-md font-body-md text-on-surface-variant line-through">
                    ${product.basePrice.toFixed(2)}
                  </span>
                  <span className="px-2 py-1 bg-error-container text-on-error-container text-label-md font-label-md rounded-lg">
                    -{product.discountPercentage}% OFF
                  </span>
                </>
              )}
            </div>

            <ProductActions colors={product.colors} />
          </div>
        </section>

        {/* Description Section */}
        <section className="mt-stack-lg border-t border-outline-variant">
          <div className="flex border-b border-outline-variant overflow-x-auto">
            <button className="px-8 py-5 text-label-md font-label-md border-b-2 border-primary text-primary uppercase tracking-widest whitespace-nowrap">
              Description
            </button>
            <button className="px-8 py-5 text-label-md font-label-md text-on-surface-variant hover:text-primary transition-colors uppercase tracking-widest whitespace-nowrap">
              Specifications
            </button>
            <button className="px-8 py-5 text-label-md font-label-md text-on-surface-variant hover:text-primary transition-colors uppercase tracking-widest whitespace-nowrap">
              Reviews ({product.reviewsCount})
            </button>
          </div>
          
          <div className="py-stack-md">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-stack-lg items-center">
              <div className="flex flex-col gap-4">
                <h3 className="text-headline-md font-headline-md">Experience Pure Silence.</h3>
                <p className="text-body-lg text-on-surface-variant leading-relaxed">
                  {product.description}
                </p>
                <p className="text-body-md text-on-surface-variant">
                  From the bustling city streets to the hum of a jet engine, focus only on what matters: your sound. With up to 60 hours of battery life and studio-grade high-fidelity drivers, the Elite 5 isn&apos;t just a gadget—it&apos;s an investment in your auditory lifestyle.
                </p>
              </div>
              <div className="rounded-2xl overflow-hidden shadow-xl border border-outline-variant relative h-[400px]">
                <Image
                  src="https://lh3.googleusercontent.com/aida-public/AB6AXuCaFPVcjmQk6wwVbzCppn59KRIgJhhnYqIt3iY0gtRc4bTjj2n__mZo6iRZq6lfBys-nG7ECkpuSasymdiWTGq53owVFSyTi721Ar3cdZUDwMUHrHjXzyzA721k08RQvijPKnJc6cvCQqKx-sCfAQc6bTFBpFqSo5jT7VDRuPGp6a18nY4wydYeGo3-SqN4-ZsFVy52AErHJ9BMdi67hI1tMkAp88MCvTjfL3pa2Z_7H1AtQCzvgFj2nkSwP7Gqs4xa0ImfYmnAi9Y"
                  alt="Lifestyle image"
                  fill
                  className="object-cover"
                  sizes="(max-width: 768px) 100vw, 50vw"
                />
              </div>
            </div>
          </div>
        </section>

        <FrequentlyBoughtTogether 
          currentProduct={{
            id: product.id,
            name: product.name.split(' - ')[0],
            price: product.discountPrice || product.basePrice,
            imageUrl: product.imageUrl
          }}
          suggestedItems={suggestedItems}
        />
      </main>

      <Footer />
    </div>
  );
}
