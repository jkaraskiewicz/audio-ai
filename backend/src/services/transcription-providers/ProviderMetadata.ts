import { TranscriptionProvider } from '../../interfaces/AudioTranscriptionProvider';

/**
 * Provider metadata describing capabilities and requirements
 */
export interface ProviderMetadata {
  cost: 'free' | 'paid' | 'freemium';
  installation: 'none' | 'api_key' | 'docker' | 'pip';
  performance: 'low' | 'medium' | 'high';
  accuracy: 'low' | 'medium' | 'high';
}

/**
 * Provider registry entry containing all information needed to create and describe a provider
 */
export interface ProviderRegistryEntry {
  name: TranscriptionProvider;
  description: string;
  constructor?: new (...args: any[]) => any;
  factory?: (config: any) => any;
  requiresApiKey: boolean;
  supportsModels: boolean;
  supportedFormats: string[];
  maxFileSize: number;
  isAvailable: boolean;
  metadata: ProviderMetadata;
}
